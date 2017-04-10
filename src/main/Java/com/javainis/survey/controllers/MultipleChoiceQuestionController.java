package com.javainis.survey.controllers;

import com.javainis.survey.dao.ChoiceDAO;
import com.javainis.survey.entities.Choice;
import com.javainis.survey.entities.FreeTextQuestion;
import com.javainis.survey.entities.MultipleChoiceQuestion;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class MultipleChoiceQuestionController {
    @Getter
    private MultipleChoiceQuestion question = new MultipleChoiceQuestion();

    @Inject
    private NewSurveyController surveyController;

    @Getter
    private ChoiceDAO choiceDAO;

    public void addChoice(Choice choice){
        question.getChoices().add(choice);
    }

    public void removeChoice(Choice choice){
        question.getChoices().remove(choice);
    }

    public void saveChoice(Choice choice)
    {
        if(!question.getChoices().contains(choice)){
            choice.setQuestion(question);
            addChoice(choice);
        }else{
            Messages.addGlobalInfo("Duplicate choice is already in question.");
        }
    }

    @PostConstruct
    private void postConstruct(){
        // Check if edit question
        if(surveyController.getSurveyCreationStep() == NewSurveyController.SURVEY_CREATION_STEP.EDIT_QUESTION){
            question = (MultipleChoiceQuestion) surveyController.getQuestionToEdit();
        }
    }
}
