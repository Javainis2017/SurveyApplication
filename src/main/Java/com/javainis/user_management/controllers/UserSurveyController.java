package com.javainis.user_management.controllers;

import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Survey;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;


@Named
@RequestScoped
public class UserSurveyController {
    @Inject
    private UserController userController;

    @Inject
    private SurveyDAO surveyDAO;

    public List<Survey> getAllUserSurvey(){
        return surveyDAO.findByAuthorId(userController.getUser().getUserID());
    }



}
