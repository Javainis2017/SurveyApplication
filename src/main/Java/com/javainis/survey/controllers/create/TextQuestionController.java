package com.javainis.survey.controllers.create;

import com.javainis.survey.entities.FreeTextQuestion;
import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private Boolean required;

    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (FreeTextQuestion) surveyController.getQuestionToEdit();
            text = question.getText();
            required = question.getRequired();
        }

    }

    public void saveQuestion(){
        question.setText(text);
        question.setRequired(required);

        surveyController.saveQuestion(question);
    }
}
