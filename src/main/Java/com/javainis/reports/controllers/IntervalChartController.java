package com.javainis.reports.controllers;

import com.javainis.reports.api.IntervalQuestionReport;

public class IntervalChartController implements IntervalQuestionReport {
    @Override
    public String getTemplateName() {
        return "interval-show.xhtml";
    }
}
