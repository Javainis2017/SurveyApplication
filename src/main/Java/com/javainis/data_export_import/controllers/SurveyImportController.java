package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.controllers.create.NewSurveyController;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Question;
import com.javainis.survey.entities.Survey;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.entities.User;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.omnifaces.util.Messages;

/**
 * Created by Ignas on 2017-04-26.
 */
@Named
@RequestScoped
public class SurveyImportController {

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
    private List<Answer> surveyAnswers;

    @Transactional
    public void importSurvey(){
        File file = new File("survey.xlsx");
        selectedSurvey = dataImporter.importSurvey(file);
        selectedSurvey.setDescription("This survey is imported from file: survey.xlsx");
        selectedSurvey.setTitle("survey.xlsx");
        List <Question> questions = selectedSurvey.getQuestions();
        for  (int i = 0; i < selectedSurvey.getQuestions().size(); i++){
            System.out.println(questions.get(i).getText() + " " + i);
        }
        saveSurvey();
    }

    @Transactional
    public void importAnswers(){
        // You can only import answers with survey
        File file = new File("survey.xlsx");
        List <Answer> answers = dataImporter.importAnswers(file, selectedSurvey);

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

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Transactional
    public void saveAnswers(){

    }

}
