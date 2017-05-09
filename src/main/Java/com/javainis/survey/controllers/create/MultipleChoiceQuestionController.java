package com.javainis.survey.controllers.create;

import com.javainis.survey.entities.Choice;
import com.javainis.survey.entities.MultipleChoiceQuestion;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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

    @Inject
    private NewSurveyController surveyController;

    @Getter
    private Choice choice = new Choice();

    @Getter
    @Setter
    private String choiceText;

    @Getter
    private boolean edit = false;

    public String getSaveButtonName(){
        if(edit){
            return "Save changes";
        }
        return "Save choice";
    }

    public void editChoice(Choice choice){
        this.choice = choice;
        choiceText = choice.getText();
        edit = true;
    }

    public void cancelEdit(){
        edit = false;
        choice = new Choice();
        choiceText = "";
    }

    public void addChoice(Choice choice){
        question.getChoices().add(choice);
    }

    public void removeChoice(Choice choice){
        question.getChoices().remove(choice);
    }

    public void saveChoice()
    {
        /* Check for duplicate choice text */
        int choiceCount = 0;
        for (Choice choice : question.getChoices()){
            if(choice.getText().equals(choiceText)){
                choiceCount++;
            }
        }
        if(choiceCount >= 1){
            FacesContext.getCurrentInstance().addMessage("multipleChoiceMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Question cannot have duplicate choices.", "Question cannot have duplicate choices."));
            return;
        }

        if(!edit) {
            choice.setText(choiceText);
            choice.setQuestion(question);
            addChoice(choice);
        }else{
            choice.setText(choiceText);
        }
        choice = new Choice();
        choiceText = "";
        edit = false;
    }

    public void saveQuestion(){
        // Validate
        if(question.getChoices().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("multipleChoiceMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Question must have at least 1 choice.", "Question must have at least 1 choice."));
        }else {
            // Save
            surveyController.saveQuestion(question);
            // Destroy this bean
            FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("multipleChoiceQuestionController");
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
