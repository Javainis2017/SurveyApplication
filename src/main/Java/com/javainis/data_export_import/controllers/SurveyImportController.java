package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.controllers.create.NewSurveyController;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Question;
import com.javainis.survey.entities.Survey;
import com.javainis.survey.entities.SurveyResult;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.entities.User;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.Part;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.IOUtils;
import org.omnifaces.util.Messages;
import org.primefaces.model.UploadedFile;

@Named
@SessionScoped
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
        //file = new File("survey.xlsx");
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
    public void upload() throws IOException{ //taisyti
        if(uploadedFile != null) {
            FacesMessage message = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);

        }
        System.out.println(uploadedFile.getFileName() + " " + uploadedFile.getContentType());
        String filename = uploadedFile.getFileName();
        InputStream input = uploadedFile.getInputstream();

        System.out.println(uploadedFile.getContentType());
        OutputStream output = new FileOutputStream(new File("/tomee/bin", filename)); //temp folder?
        try {
            IOUtils.copy(input, output);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }

        file = new File("/tomee/bin", filename);
    }

}
