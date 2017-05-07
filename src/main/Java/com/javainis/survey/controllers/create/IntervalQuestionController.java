package com.javainis.survey.controllers.create;

import com.javainis.survey.entities.IntervalQuestion;
import lombok.Setter;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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

    public void saveQuestion(){
        if(minValue <= maxValue){
            question.setMin(minValue);
            question.setMax(maxValue);
            surveyController.saveQuestion(question);
        }else{
            FacesContext.getCurrentInstance().addMessage("intervalMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, "Minimum value cannot be higher than maximum.", "Minimum value cannot be higher than maximum."));
        }
    }
    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (IntervalQuestion) surveyController.getQuestionToEdit();
            minValue = question.getMin();
            maxValue = question.getMax();
        }
    }
}
