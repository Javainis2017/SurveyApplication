package com.javainis.reports.api;

import com.javainis.reports.mybatis.model.Question;

public interface QuestionReport {
    String getTemplateName();
    void setQuestion(Question question);
}
