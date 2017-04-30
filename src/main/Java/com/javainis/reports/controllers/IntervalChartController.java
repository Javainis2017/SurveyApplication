package com.javainis.reports.controllers;

import com.javainis.reports.api.IntervalQuestionReport;
import com.javainis.reports.mybatis.model.NumberAnswer;
import com.javainis.reports.mybatis.model.Question;
import com.javainis.survey.entities.IntervalQuestion;
import lombok.Getter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.List;
@Named
@RequestScoped
public class IntervalChartController implements IntervalQuestionReport {
    @Getter
    IntervalQuestion question;
    @Getter
    List<NumberAnswer> answers;
    @Override
    public String getTemplateName() {
        return "interval-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {

    }
}
