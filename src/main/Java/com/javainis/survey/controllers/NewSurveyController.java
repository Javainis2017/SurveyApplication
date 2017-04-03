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
    private String newQuestionType = "text";

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

    public void addQuestion(Question question){
        question.setSurvey(survey);
        survey.getQuestions().add(question);
        newQuestionType = "text2";
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

        /* surveyDAO.create(survey);*/
        System.out.println(survey.getQuestions().size());
    }
}
