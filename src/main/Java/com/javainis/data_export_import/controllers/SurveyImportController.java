package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.dao.SurveyAsyncDAO;
import com.javainis.survey.dao.SurveyResultAsyncDAO;
import com.javainis.survey.entities.Survey;
import com.javainis.survey.entities.SurveyResult;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.entities.User;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.ejb.AsyncResult;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.deltaspike.core.api.future.Futureable;
import org.apache.poi.util.IOUtils;
import org.omnifaces.util.Messages;
import org.primefaces.model.UploadedFile;

@Named
@RequestScoped
public class SurveyImportController implements Serializable{

    @Inject
    DataImporter dataImporter;

    @Inject
    private UserController userController;

    @Inject
    private RandomStringGenerator randomStringGenerator;

    @Inject
    private SurveyAsyncDAO surveyAsyncDAO;

    @Inject
    private SurveyResultAsyncDAO surveyResultAsyncDAO;

    @Getter
    private Survey selectedSurvey = new Survey();

    @Getter
    @Setter
    private String expirationDateString;

    @Getter
    @Setter
    private String expirationTimeString;

    @Getter
    private List<SurveyResult> surveyResultList = new ArrayList<>();

    @Getter
    private Future<Survey> selectedSurveyFuture;

    @Getter
    private Future<List<SurveyResult>> surveyResultListFuture;

    @Getter
    private Future<Boolean> surveyQuestionInDBFuture;

    @Getter
    private Future<Boolean> surveyAnswerInDBFuture;

    @Getter
    private File file;

    @Getter
    @Setter
    private UploadedFile uploadedFile;

    private boolean dateTimeError = false;

    @Transactional
    private void importSurvey(){
        dateTimeError = false;
        User currentUser = userController.getUser();
        String url = randomStringGenerator.generateString(32);
        while(surveyAsyncDAO.existsByUrl(url)){

            url = randomStringGenerator.generateString(32);
        }
        selectedSurvey.setUrl(url);
        selectedSurvey.setAuthor(currentUser);
        if (selectedSurvey.getTitle().trim().equals("")) {
            selectedSurvey = null;
            return;
        }
        if (!expirationDateString.isEmpty()) {
            Timestamp timestamp;
            try {
                timestamp = convertToExpirationTimestamp(expirationDateString, expirationTimeString);
            } catch (Exception e) {
                Messages.addGlobalWarn("Wrong expiration time.");
                dateTimeError = true;
                selectedSurvey.setExpirationTime(null);
                selectedSurvey = null;
                return;
            }
            selectedSurvey.setExpirationTime(timestamp);
        }

        if (!expirationTimeString.isEmpty() && expirationDateString.isEmpty()) {
            Messages.addGlobalWarn("Cannot set time without date.");
            dateTimeError = true;
            selectedSurvey = null;
            return;
        }

        selectedSurveyFuture = dataImporter.importSurvey(file, selectedSurvey);

        try{
            selectedSurvey = selectedSurveyFuture.get();

            if (file == null || selectedSurvey == null) return;
            if (selectedSurvey.getQuestions() == null || selectedSurvey.getQuestions().isEmpty()){
                selectedSurvey = null;
                return;
            }
            //Async
            surveyResultListFuture = dataImporter.importAnswers(file, selectedSurvey, surveyResultList);
            selectedSurvey.setSurveyResults(surveyResultList);

            //Async
            surveyQuestionInDBFuture = saveSurvey(selectedSurvey);

            surveyResultList = surveyResultListFuture.get();
            if (surveyResultList == null) selectedSurvey.setSurveyResults(null);
            if (surveyQuestionInDBFuture.get() && surveyResultList != null){
                surveyAnswerInDBFuture = saveAnswers(surveyResultList);
            }


        } catch (InterruptedException e){
            e.printStackTrace();
            selectedSurvey = null;
        } catch (ExecutionException e){
            e.printStackTrace();
            selectedSurvey = null;
        }

    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Futureable
    private Future<Boolean> saveSurvey(Survey survey){

        try {
            surveyAsyncDAO.create(survey); //ConstraintViolationException:
        }catch (Exception e){
            e.printStackTrace();
            return new AsyncResult<>(false);

        }
        return new AsyncResult<>(true);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Futureable
    private Future<Boolean> saveAnswers(List<SurveyResult> surveyResults){
        try{
            for (SurveyResult result : surveyResults){
                result.setComplete(true);
                surveyResultAsyncDAO.create(result);
            }
            return new AsyncResult<>(true);
        }
        catch (Exception e){
            e.printStackTrace();
            return new AsyncResult<>(false);
        }
    }

    @Transactional
    public String upload() throws InterruptedException, ExecutionException{
        String xlsxContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (uploadedFile == null){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Problem", "Failed to upload");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }
        if (!uploadedFile.getContentType().equals(xlsxContentType)){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Failed", uploadedFile.getFileName() + " not xlsx format.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }
        Path path;
        try  {
            path = Files.createTempDirectory("temp");
            String filename = uploadedFile.getFileName();
            InputStream input = uploadedFile.getInputstream();
            OutputStream output = new FileOutputStream(new File(path.toString(), filename));
            IOUtils.copy(input, output);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
            file = new File(path.toString(), filename);
        }catch (IOException e){
            return null;
        }

        importSurvey();

        if (selectedSurvey == null && surveyResultList == null){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Problem", "Failed to import survey questions and answers.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            cleanTempFolder(path);
            return null;
        } else if (selectedSurvey == null){
            if(!dateTimeError){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Problem", "Failed to import survey questions.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            cleanTempFolder(path);
            return null;
        }

        cleanTempFolder(path);

        selectedSurvey = new Survey();
        surveyResultList = new ArrayList<>();
        setUploadedFile(null);
        return "/home?faces-redirect=true";
    }

    private void cleanTempFolder(Path path){
        try{
            file.delete();
            Files.delete(path);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private Timestamp convertToExpirationTimestamp(String date, String time) {
        String fullDate;

        if (!time.isEmpty()) {
            fullDate = date + " " + time + ":00";
        } else {
            fullDate = date + " 23:59:59";
        }

        return Timestamp.valueOf(fullDate);
    }
}
