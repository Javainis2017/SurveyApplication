package com.javainis.reports.api;

import com.javainis.reports.mybatis.model.Question;

import java.util.concurrent.Future;

public interface QuestionReport {
    String getTemplateName();
    void setQuestion(Question question);
    Future<Void> generateReportAsync();
}
