package com.javainis.survey.controllers;

import com.javainis.survey.entities.FreeTextQuestion;
import com.javainis.survey.entities.IntervalQuestion;
import lombok.Setter;
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

    @Getter
    @Setter
    private int minValue;

    @Getter
    @Setter
    private int maxValue;

/*
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
*/
    public void saveQuestion(){
        if(minValue <= maxValue){
            question.setMinValue(minValue);
            question.setMaxValue(maxValue);
            surveyController.saveQuestion(question);
        }else{
            Messages.addGlobalInfo("Minimum value must not be higher than maximum");
        }
    }
    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (IntervalQuestion) surveyController.getQuestionToEdit();
            minValue = question.getMinValue();
            maxValue = question.getMaxValue();
        }
    }
}
