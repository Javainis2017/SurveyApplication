package com.javainis.survey.controllers.create;

import com.javainis.survey.dao.QuestionDAO;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Question;
import com.javainis.survey.entities.Survey;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.entities.User;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;

@Named
@ViewScoped
public class NewSurveyController implements Serializable{

    public enum SURVEY_CREATION_STEP {
        QUESTION_TYPE_CHOICE, NEW_QUESTION, EDIT_QUESTION
    }
    @Inject
    private SurveyDAO surveyDAO;

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    private UserController userController;

    @Inject
    private RandomStringGenerator randomStringGenerator;

    @Getter
    private Survey survey = new Survey();

    @Getter
    private SURVEY_CREATION_STEP surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;

    /* Naujai kuriamo klausimo tipas*/
    @Getter
    private String newQuestionType;

    @Getter
    private Question questionToEdit;

    public void createQuestion(String type){
        surveyCreationStep = SURVEY_CREATION_STEP.NEW_QUESTION;
        newQuestionType = type;
    }

    public void saveQuestion(Question question){
        //Check if question is new
        if(surveyCreationStep == SURVEY_CREATION_STEP.NEW_QUESTION){
            question.setSurvey(survey);
            survey.getQuestions().add(question);
        }
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        questionToEdit = null;
    }

    public void editQuestion(Question question){
        surveyCreationStep = SURVEY_CREATION_STEP.EDIT_QUESTION;
        questionToEdit = question;
    }

    public void removeQuestion(Question question){
        question.setSurvey(null);
        survey.getQuestions().remove(question);
    }

    @Transactional
    public String createSurvey(){
        /* Check if survey has questions */
        if(survey.getQuestions().isEmpty()){
            Messages.addGlobalInfo("Survey must have at least 1 question.");
            return null;
        }

        /* Generate unique URL*/
        String url = randomStringGenerator.generateString(32);
        System.out.println(url);
        // Check if url is duplicate
        while(surveyDAO.existsByUrl(url)){
            url = randomStringGenerator.generateString(32);
        }
        survey.setUrl(url);

        User currentUser = userController.getUser();
        survey.setAuthor(currentUser);

        /* currentUser.getSurveys().add(survey); */

        /* Persist questions/cascade */
        try {
            surveyDAO.create(survey);

        }catch (Exception e){
            e.printStackTrace();

        }
        return "/home-page?faces-redirect=true";
    }
}
