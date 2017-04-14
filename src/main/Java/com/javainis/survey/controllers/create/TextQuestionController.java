package com.javainis.survey.controllers.create;

import com.javainis.survey.entities.FreeTextQuestion;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
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
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (FreeTextQuestion) surveyController.getQuestionToEdit();
        }
    }
}