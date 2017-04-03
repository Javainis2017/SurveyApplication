package com.javainis.survey.controllers;

import com.javainis.survey.entities.FreeTextQuestion;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class TextQuestionController {

    @Getter
    private FreeTextQuestion question = new FreeTextQuestion();

    @PostConstruct
    private void postConstruct(){
        System.out.println("TextQuestionController postConstruct");
        System.out.println(question.getText());
    }
    @PreDestroy
    private void preDestroy(){
        System.out.println("TextQuestionController preDestroy");
        System.out.println(question.getText());
    }
}
