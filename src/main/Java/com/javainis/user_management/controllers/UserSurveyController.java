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
import java.util.ArrayList;
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

    // skirtas atrinkti naudotojui rodomoms apklausoms
    @Transactional
    public List<Survey> getMySurveys(){
        if (isAdmin()){
            return sortSurvey(getAllSurveys());
        }
        List<Survey> mySurveys = getAllUserSurvey();
        List<Survey> publicSurveys = getAllPublicSurvey();

        for (Survey s : publicSurveys){
            if (!mySurveys.contains(s)) mySurveys.add(s);
        }
        return sortSurvey(mySurveys);
    }

    private List<Survey> sortSurvey(List<Survey> surveys){

        List <Survey> sortedSurveyList = new ArrayList<>();

        for (Survey s: surveys){
            if (userController.getUser().equals(s.getAuthor())){
                sortedSurveyList.add(s);
            }
        }
        surveys.removeAll(sortedSurveyList);
        sortedSurveyList.addAll(surveys);

        return sortedSurveyList;
    }

    @Transactional
    public Boolean canSeeReport(Survey survey){
        if (survey.getSurveyResults().isEmpty()) {
            return false;
        }
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

    private Boolean isAdmin(){
        // 1 - Admin, 2 - User
        return userController.getUser().getUserType().getId() == UserTypeDAO.USER_TYPE_ADMIN;
    }

    public String getPublicLabel(Boolean isPublic){
        if (isPublic) return "Public";
        return "Private";
    }

}
