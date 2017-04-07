package com.javainis.survey.controllers;

import com.javainis.survey.entities.FreeTextQuestion;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class TextQuestionController {

    @Getter
    private FreeTextQuestion question = new FreeTextQuestion();

    @Inject
    private NewSurveyController surveyController;

    @PostConstruct
    private void postConstruct(){
        if(surveyController.getQuestionToEdit() != null){
            question = (FreeTextQuestion) surveyController.getQuestionToEdit();
            System.out.println("Edited question: " + question.getText());
        }
    }
}
