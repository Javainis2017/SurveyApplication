package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.Survey;
import com.javainis.survey.entities.SurveyResult;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.entities.User;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    private SurveyDAO surveyDAO;

    @Inject
    private SurveyResultDAO surveyResultDAO;

    @Getter
    private Survey selectedSurvey = new Survey();

    @Getter
    private List<SurveyResult> surveyResultList = new ArrayList<>();

    @Getter
    private File file;

    @Getter
    @Setter
    private UploadedFile uploadedFile;

    @Transactional
    private void importSurvey(){
        selectedSurvey = dataImporter.importSurvey(file);
        if (selectedSurvey == null) return;
        selectedSurvey.setDescription("This survey is imported from file: " + file.getName());
        selectedSurvey.setTitle(file.getName());
        selectedSurvey.setIsPublic(true);
        saveSurvey();
    }

    @Transactional
    private void importAnswers(){
        // You can only import answers with survey
        selectedSurvey.setSurveyResults(surveyResultList);
        surveyResultList = dataImporter.importAnswers(file, selectedSurvey);
        if (surveyResultList == null) return;
        saveAnswers();
    }

    @Transactional
    private void saveSurvey(){
        /* Generate unique URL*/
        String url = randomStringGenerator.generateString(32);
        System.out.println(url);
        // Check if url is duplicate
        while(surveyDAO.existsByUrl(url)){
            url = randomStringGenerator.generateString(32);
        }
        selectedSurvey.setUrl(url);

        User currentUser = userController.getUser();
        selectedSurvey.setAuthor(currentUser);

        /* currentUser.getSurveys().add(survey); */

        /* Persist questions/cascade */
        try {
            surveyDAO.create(selectedSurvey);

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Transactional
    private void saveAnswers(){
        try{
            for (SurveyResult result : surveyResultList){
                surveyResultDAO.create(result);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public Boolean upload(){
        String xlsxContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (!uploadedFile.getContentType().equals(xlsxContentType)){
            FacesMessage message = new FacesMessage("Failed", uploadedFile.getFileName() + " is not xlsx format.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return false;
        }
        if(uploadedFile != null) {
            FacesMessage message = new FacesMessage(uploadedFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else return false;

        System.out.println(uploadedFile.getFileName() + "*** " + uploadedFile.getContentType());
        try  {
            Path path = Files.createTempDirectory("temp");
            String filename = uploadedFile.getFileName();
            InputStream input = uploadedFile.getInputstream();
            OutputStream output = new FileOutputStream(new File(path.toString(), filename));

            IOUtils.copy(input, output);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);

            file = new File(path.toString(), filename);
        }catch (IOException e){
            return false;
        }
        importSurvey();
        if (selectedSurvey == null) return false;
        importAnswers();

        selectedSurvey = null;
        surveyResultList = null;
        setUploadedFile(null);
        return true;
    }
}
