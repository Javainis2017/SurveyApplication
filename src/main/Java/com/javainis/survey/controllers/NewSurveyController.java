package com.javainis.survey.controllers;

import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Question;
import com.javainis.survey.entities.Survey;
import lombok.Getter;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class NewSurveyController implements Serializable{

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
    private String newQuestionType;

    @Getter
    private Boolean newQuestion = true;

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
        newQuestion = false;
        newQuestionType = type;
    }

    public void saveQuestion(Question question){
        //Check if question is new
        if(!survey.getQuestions().contains(question)){
            //Question is new
            question.setSurvey(survey);
            survey.getQuestions().add(question);
        }else{
            //Question is new or same
        }
        newQuestion = true;
        questionToEdit = null;
    }

    public void editQuestion(Question question){
        newQuestion = false;
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
