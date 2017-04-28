package com.javainis.reports.controllers;


import com.javainis.reports.mybatis.dao.SurveyMapper;
import com.javainis.reports.mybatis.model.Survey;
import com.javainis.survey.controllers.create.IntervalQuestionController;
import com.javainis.survey.controllers.create.TextQuestionController;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.dao.UserTypeDAO;
import lombok.Getter;
import org.omnifaces.cdi.Param;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
@Getter
public class ReportController implements Serializable {
    @Inject
    private UserController userController;
    @Inject
    private TextQuestionController textQuestionController;
    @Inject
    private IntervalQuestionController intervalQuestionController;

    @Inject
    private SurveyMapper surveyMapper;

    @Inject
    @Param(pathIndex = 0)
    private String surveyUrl;

    private Survey survey;

    private boolean canAccess = false;

    @PostConstruct
    private void init(){
        survey = surveyMapper.selectByUrl(surveyUrl);
        if(survey == null){
            canAccess = false;
            System.out.println("Survey null");
            return;
        }
        /*if(userController.getUser().getUserID() == survey.getAuthorId() || userController.getUser().getUserTypeID() == USER_TYPE_ADMIN || !survey.isPrivate()){
            canAccess = true;
        }*/
        if(userController.getUser().getUserID() == survey.getAuthorId() || userController.getUser().getUserType().getId() == UserTypeDAO.USER_TYPE_ADMIN){
            canAccess = true;
        }
    }
    public boolean canAccess(){
        return canAccess;
    }

}
