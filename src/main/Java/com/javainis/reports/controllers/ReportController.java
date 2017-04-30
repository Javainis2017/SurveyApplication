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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Named
@ViewScoped
@Getter
public class ReportController implements Serializable {
    private static final int TIMEOUT_LIMIT = 60;

    @Inject
    private UserController userController;

    @Inject
    private SurveyMapper surveyMapper;

    @Inject
    @Param(pathIndex = 0)
    private String surveyUrl;

    private Survey survey;

    private int refreshCount = 0;

    private boolean canAccess = false;

    private boolean timeout = false;

    private Map<Question, QuestionReport> questionReports;

    private Map<Question, Future<Void>> reports;

    @PostConstruct
    private void init() {
        survey = surveyMapper.selectByUrl(surveyUrl);
        if (survey == null) {
            canAccess = false;
            return;
        }
        /*if(userController.getUser().getUserID() == survey.getAuthorId() || userController.getUser().getUserTypeID() == USER_TYPE_ADMIN || !survey.isPrivate()){
            userCanAccess = true;
        }*/
        if (userController.getUser().getUserID() == survey.getAuthorId() || userController.getUser().getUserType().getId() == UserTypeDAO.USER_TYPE_ADMIN) {
            canAccess = true;
        }

        /* Link questions and controllers */
        questionReports = new HashMap<>();
        reports = new HashMap<>();
        for (Question question : survey.getQuestions()) {
            QuestionReport report;
            Future<Void> future;
            if (question instanceof FreeTextQuestion) {
                report = javax.enterprise.inject.spi.CDI.current().select(TextQuestionReport.class).get();
                report.setQuestion(question);
                future = report.generateReportAsync();
                reports.put(question, future);
                questionReports.put(question, report);
            } else if (question instanceof IntervalQuestion) {
                report = javax.enterprise.inject.spi.CDI.current().select(IntervalQuestionReport.class).get();
                report.setQuestion(question);
                future = report.generateReportAsync();
                reports.put(question, future);
                questionReports.put(question, report);
            }
            //TODO: add missing reports
            /*
            report.setQuestion(question);
            questionReports.put(question, report);
            reports.add(report.generateReportAsync());*/
        }
    }

    public void checkProgress() {
        timeout = true;
        System.out.println("polling");
        refreshCount++;
        if(refreshCount >= TIMEOUT_LIMIT){
            timeout = true;
            System.out.println("timeout");
            return;
        }
        for (Future future : reports.values()) {
            if (!future.isDone()) {
                timeout = false;
            }
        }
    }
}
