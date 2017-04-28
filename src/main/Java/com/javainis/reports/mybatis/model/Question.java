package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class Question {

    private Long id;
    private String text;
    private Boolean required;
    private Long surveyId;
    private Long questionTypeId;

    private List<Answer> answers;

}