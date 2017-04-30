package com.javainis.reports.controllers;

import com.javainis.reports.api.IntervalQuestionReport;
import com.javainis.reports.api.QuestionReport;
import com.javainis.reports.api.TextQuestionReport;
import com.javainis.reports.mybatis.dao.SurveyMapper;
import com.javainis.reports.mybatis.model.FreeTextQuestion;
import com.javainis.reports.mybatis.model.IntervalQuestion;
import com.javainis.reports.mybatis.model.Question;
import com.javainis.reports.mybatis.model.Survey;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.dao.UserTypeDAO;
import lombok.Getter;
import org.omnifaces.cdi.Param;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Named
@ViewScoped
@Getter
public class ReportController implements Serializable {

    @Inject
    private UserController userController;

    @Inject
    private SurveyMapper surveyMapper;

    @Inject
    @Param(pathIndex = 0)
    private String surveyUrl;

    private Survey survey;

    private boolean canAccess = false;

    private Map<Question, QuestionReport> questionReports;

    @PostConstruct
    private void init(){
        survey = surveyMapper.selectByUrl(surveyUrl);
        if(survey == null){
            canAccess = false;
            return;
        }
        /*if(userController.getUser().getUserID() == survey.getAuthorId() || userController.getUser().getUserTypeID() == USER_TYPE_ADMIN || !survey.isPrivate()){
            userCanAccess = true;
        }*/
        if(userController.getUser().getUserID() == survey.getAuthorId() || userController.getUser().getUserType().getId() == UserTypeDAO.USER_TYPE_ADMIN){
            canAccess = true;
        }

        /* Link questions and controllers */
        questionReports = new HashMap<>();
        for(Question question : survey.getQuestions()){
            QuestionReport report;
            if(question instanceof FreeTextQuestion){
                report = javax.enterprise.inject.spi.CDI.current().select(TextQuestionReport.class).get();
                report.setQuestion(question);
                questionReports.put(question, report);
            }else if(question instanceof IntervalQuestion){
                report = javax.enterprise.inject.spi.CDI.current().select(IntervalQuestionReport.class).get();
                report.setQuestion(question);
                questionReports.put(question, report);
            }
            /*
            report.setQuestion(question);
            questionReports.put(question, report);*/
        }
    }

    @PreDestroy
    private void preDestroy(){
        for(QuestionReport report : questionReports.values()){
            javax.enterprise.inject.spi.CDI.current().select(QuestionReport.class).destroy(report);
        }
    }
}
