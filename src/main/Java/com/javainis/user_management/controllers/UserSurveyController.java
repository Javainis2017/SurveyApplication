package com.javainis.user_management.controllers;

import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Survey;
import com.javainis.user_management.dao.UserTypeDAO;
import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
    private Survey selectedSurvey;

    @Getter
    @Setter
    private List<Survey> filteredSurveys;

    public List<Survey> getAllUserSurvey(){
        return surveyDAO.findByAuthorId(userController.getUser().getUserID());
    }

    public List<Survey> getAllSurveys()
    {
        return surveyDAO.getAll();
    }

    public List<Survey> getAllPublicSurvey(){
        return surveyDAO.findPublic();
    }

    // skirtas atrinktas user rodomas survey
    @Transactional
    public List<Survey> getMySurveys(){
        if (isAdmin()) return getAllSurveys();
        List<Survey> mySurveys = getAllUserSurvey();
        List<Survey> publicSurveys = getAllPublicSurvey();

        for (Survey s : publicSurveys){
            if (!mySurveys.contains(s)) mySurveys.add(s);
        }
        return mySurveys;
    }

    @Transactional
    public Boolean canSeeReport(Survey survey){
        return survey.getAuthor().equals(userController.getUser()) || isAdmin() || survey.getIsPublic();
    }

    @Transactional
    public Boolean canRemove(Survey survey){
        if (survey == null)
        {
            return false;
        }

        return survey.getAuthor().equals(userController.getUser()) || isAdmin();
    }

    // Useless? Admin can change public/ private with Edit Survey.
    @Transactional
    public Boolean ToggleSurveyPublicStatus(Survey survey){
        if (survey.getIsPublic()) survey.setIsPublic(false);
        else survey.setIsPublic(true);
        surveyDAO.update(survey);
        return true;
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
