package com.javainis.survey.controllers;

import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Question;
import com.javainis.survey.entities.Survey;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class NewSurveyController implements Serializable{

    public enum SURVEY_CREATION_STEP {
        QUESTION_TYPE_CHOICE, NEW_QUESTION, EDIT_QUESTION
    }
    @Inject
    private SurveyDAO surveyDAO;

    /*@Inject
    private QuestionDAO questionDAO;

    @Inject
    private QuestionTypeDAO questionTypeDAO;

    @Inject
    private UserController userController;
    */

    @Getter
    private Survey survey = new Survey();

    @Getter
    private SURVEY_CREATION_STEP surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;

    @Getter
    private String newQuestionType;

    @Getter
    private Question questionToEdit;

    /* Naujai kuriamo klausimo tipas
    @Getter
    @Setter
    private QuestionType newQuestionType;*/

    /*public void createQuestion(QuestionType questionType){
        newQuestionType = questionType;
    }

    public List<QuestionType> getAllQuestionTypes(){
        return questionTypeDAO.getAll();
    }*/

    public void createQuestion(String type){
        surveyCreationStep = SURVEY_CREATION_STEP.NEW_QUESTION;
        newQuestionType = type;
    }

    public void saveQuestion(Question question){
        //Check if question is new
        if(surveyCreationStep == SURVEY_CREATION_STEP.NEW_QUESTION){
            // Check for duplicate question
            if(!survey.getQuestions().contains(question)){
                //Question is not duplicate
                question.setSurvey(survey);
                survey.getQuestions().add(question);
            }else{
                // Question is duplicate
                Messages.addGlobalInfo("Duplicate question is already in survey.");
            }
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

    public void createSurvey(){
        /* Generate unique URL*/
        /* survey.setUrl(url); */

        /* User currentUser = userController.getCurrentUser();
        * survey.setAuthor(currentUser);
        * currentUser.getSurveys().add(survey); */

        /* Persist questions/cascade */
        /* surveyDAO.create(survey);*/
    }
}
