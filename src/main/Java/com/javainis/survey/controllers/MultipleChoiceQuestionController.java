package com.javainis.survey.controllers;

import com.javainis.survey.entities.Choice;
import com.javainis.survey.entities.FreeTextQuestion;
import com.javainis.survey.entities.MultipleChoiceQuestion;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class MultipleChoiceQuestionController implements Serializable {
    @Getter
    private MultipleChoiceQuestion question = new MultipleChoiceQuestion();
    @Getter
    @Setter
    private String[] answers;
    @Inject
    private NewSurveyController surveyController;

    @Getter
    private Choice choice = new Choice();

    private boolean edit = false;

    public void editChoice(Choice choice){
        System.out.println(choice.getText());
        this.choice = choice;
        edit = true;
    }

    public void addChoice(Choice choice){
        question.getChoices().add(choice);
    }

    public void removeChoice(Choice choice){
        question.getChoices().remove(choice);
    }

    public void saveChoice()
    {
        if(!edit) {
            choice.setQuestion(question);
            addChoice(choice);
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
            FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("multiChoiceQuestionController");
        }
    }

    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (MultipleChoiceQuestion) surveyController.getQuestionToEdit();
        }else if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.NEW_QUESTION){
            question = new MultipleChoiceQuestion();
            choice = new Choice();
        }
    }
}
