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

    @Inject
    private NewSurveyController surveyController;

    @Getter
    private IntervalQuestion question = new IntervalQuestion();

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private Boolean required;

    @Getter
    @Setter
    private int minValue;

    @Getter
    @Setter
    private int maxValue;

    public void saveQuestion(){
        if(text.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("intervalMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, "Question cannot be empty.", "Question cannot be empty."));
        }else if(minValue > maxValue){
            FacesContext.getCurrentInstance().addMessage("intervalMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, "Minimum value cannot be higher than maximum.", "Minimum value cannot be higher than maximum."));
        }else{
            question.setText(text);
            question.setRequired(required);
            question.setMin(minValue);
            question.setMax(maxValue);
            surveyController.saveQuestion(question);
        }
    }
    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (IntervalQuestion) surveyController.getQuestionToEdit();
            text = question.getText();
            required = question.getRequired();
            minValue = question.getMin();
            maxValue = question.getMax();
        }
    }
}
