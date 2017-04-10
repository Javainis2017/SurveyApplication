package com.javainis.survey.controllers;

import com.javainis.survey.entities.FreeTextQuestion;
import com.javainis.survey.entities.IntervalQuestion;
import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class IntervalQuestionController {
    @Getter
    private IntervalQuestion question = new IntervalQuestion();

    @Inject
    private NewSurveyController surveyController;

    private int minimum_value;

    private int maximum_value;

    private void setMinimum(int value){
        if(value<=maximum_value){
            question.setMin_value(value);
        }else{
            Messages.addGlobalInfo("Minimum value must not be higher than maximum");
        }
    }

    private void setMaximum(int value){
        if(value>=minimum_value){
            question.setMax_value(value);
        }else{
            Messages.addGlobalInfo("Maximum value must not be lower than minimum");
        }
    }
    public void validate(){
        setMinimum(minimum_value);
        setMaximum(maximum_value);
        surveyController.saveQuestion(question);
    }
    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (IntervalQuestion) surveyController.getQuestionToEdit();
        }
    }
}
