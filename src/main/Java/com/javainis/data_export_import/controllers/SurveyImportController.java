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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.IOUtils;
import org.omnifaces.util.Messages;
import org.primefaces.model.UploadedFile;

@Named
@ViewScoped
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
    public void importSurvey(){
        file = new File("survey.xlsx");
        selectedSurvey = dataImporter.importSurvey(file);;
        selectedSurvey.setDescription("This survey is imported from file: " + file.getName());
        selectedSurvey.setTitle(file.getName()); // koki title?
        selectedSurvey.setIsPublic(true);
        System.out.println(selectedSurvey.getId() + "   Before save");
        saveSurvey();
        System.out.println(selectedSurvey.getId() + "   After import DB");
        //importAnswers(); // jeigu iškart importuoti ir atsakymus
    }

    @Transactional
    public void importAnswers(){
        // You can only import answers with survey
        selectedSurvey.setSurveyResults(surveyResultList);
        surveyResultList = dataImporter.importAnswers(file, selectedSurvey);
        saveAnswers();
    }

    @Transactional
    private void saveSurvey(){
        /* Check if survey has questions */
        if(selectedSurvey.getQuestions().isEmpty()){
            Messages.addGlobalInfo("Imported survey must have at least 1 question.");
        }

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
            FacesMessage message = new FacesMessage("Succesful", selectedSurvey.getTitle() + " has been added to the system.");
            FacesContext.getCurrentInstance().addMessage(null, message);

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
            FacesMessage message = new FacesMessage("Succesful","Survey answers has been added to the system.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public Boolean upload(){
        if(uploadedFile != null) {
            FacesMessage message = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        String xlsxContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (!uploadedFile.getContentType().equals(xlsxContentType)) return false;
        System.out.println(uploadedFile.getFileName() + "*** " + uploadedFile.getContentType());
        try  {
            String filename = uploadedFile.getFileName();
            InputStream input = uploadedFile.getInputstream();
            OutputStream output = new FileOutputStream(new File("/tomee/bin", filename)); //temp folder?

            IOUtils.copy(input, output);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
            File dir = new File("/SurveyApp");
            dir.mkdir();
            file = new File("/SurveyApp", filename);
        }catch (IOException e){
            return false;
        }
        return true;
    }
}
