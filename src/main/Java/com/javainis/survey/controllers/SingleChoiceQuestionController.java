package com.javainis.survey.controllers;

import com.javainis.survey.dao.ChoiceDAO;
import com.javainis.survey.entities.Choice;
import com.javainis.survey.entities.FreeTextQuestion;
import com.javainis.survey.entities.SingleChoiceQuestion;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class SingleChoiceQuestionController {
    @Getter
    private SingleChoiceQuestion question = new SingleChoiceQuestion();

    @Inject
    private NewSurveyController surveyController;

    @Getter
    private ChoiceDAO choiceDAO;

    public void addChoice(Choice choice){
        question.getAnswers().add(choice);
    }

    public void removeChoice(Choice choice){
        question.getAnswers().remove(choice);
    }

    public void saveChoice(Choice choice)
    {
        if(!question.getAnswers().contains(choice)){
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
            question = (SingleChoiceQuestion) surveyController.getQuestionToEdit();
        }
    }
}
