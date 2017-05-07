package com.javainis.survey.controllers.create;

import com.javainis.survey.entities.Choice;
import com.javainis.survey.entities.SingleChoiceQuestion;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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

    @Getter
    @Setter
    private String choiceText;

    @Getter
    private boolean edit = false;

    public void editChoice(Choice choice){
        this.choice = choice;
        choiceText = choice.getText();
        edit = true;
    }

    public void removeChoice(Choice choice){
        question.getChoices().remove(choice);
    }

    public void cancelEdit(){
        edit = false;
        choiceText = "";
        choice = new Choice();
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
            FacesContext.getCurrentInstance().addMessage("singleChoiceMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, "Duplicate choice", "Question cannot have duplicate choices."));
            return;
        }

        if(!edit){
            choice.setText(choiceText);
            choice.setQuestion(question);
            question.getChoices().add(choice);
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
            FacesContext.getCurrentInstance().addMessage("singleChoiceMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, "No choices", "Question must have at least 1 choice."));
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
