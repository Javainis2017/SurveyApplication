package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SurveyResult {

    private Long id;
    private Long surveyId;

    private List<Answer> answers;

}