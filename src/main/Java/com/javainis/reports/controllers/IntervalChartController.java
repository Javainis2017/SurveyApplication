package com.javainis.reports.controllers;

import com.javainis.reports.api.IntervalQuestionReport;
import com.javainis.reports.mybatis.model.Question;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import java.io.Serializable;

@Alternative
@Dependent
public class IntervalChartController implements IntervalQuestionReport, Serializable {
    @Override
    public String getTemplateName() {
        return "interval-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {

    }
}
