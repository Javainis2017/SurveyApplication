package com.javainis.survey.controllers;

import com.javainis.survey.entities.Choice;
import com.javainis.survey.entities.SingleChoiceQuestion;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class SingleChoiceQuestionController implements Serializable{

    @Inject
    private NewSurveyController surveyController;

    @Getter
    private SingleChoiceQuestion question = new SingleChoiceQuestion();

    @Getter
    private Choice choice = new Choice();

    private boolean edit = false;

    public void editChoice(Choice choice){
        this.choice = choice;
        edit = true;
    }

    public void removeChoice(Choice choice){
        question.getChoices().remove(choice);
    }

    public void saveChoice()
    {
        if(!edit){
            choice.setQuestion(question);
            question.getChoices().add(choice);
        }
        choice = new Choice();
        edit = false;
    }

    public void saveQuestion(){
        // Validate
        if(question.getChoices().isEmpty()){
            Messages.addGlobalInfo("Question must have at least 1 choice");
        }else {
            // Save
            surveyController.saveQuestion(question);
            // Destroy this bean
            FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("singleChoiceQuestionController");
        }
    }

    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (SingleChoiceQuestion) surveyController.getQuestionToEdit();
        }else if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.NEW_QUESTION){
            question = new SingleChoiceQuestion();
            choice = new Choice();
        }
    }
}
