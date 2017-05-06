package com.javainis.user_management.controllers;

import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Survey;
import com.javainis.user_management.dao.UserTypeDAO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
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

    public List<Survey> getAllSurveys()
    {
        return surveyDAO.getAll();
    }


    @Transactional
    public Boolean ToggleSurveyPublicStatus(Survey survey){
        if (survey.getIsPublic()) survey.setIsPublic(false);
        else survey.setIsPublic(true);
        surveyDAO.update(survey);
        return true;
    }

    public Boolean canSeeReport(Survey survey){
        System.out.println(survey == null);
        return survey.getAuthor() == userController.getUser() || isAdmin() || survey.getIsPublic();
    }

    private Boolean isAdmin(){
        // 1 - Admin, 2 - User
        return userController.getUser().getUserType().getId() == UserTypeDAO.USER_TYPE_ADMIN;
    }

    public String getPublicLabel(Boolean isPublic){
        if (isPublic) return "Public";
        return "Private";
    }

}
