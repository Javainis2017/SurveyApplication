package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Answer {

    private Long id;
    private Long resultId;
    private Long questionId;
    private Long choiceId;
    private String textAnswer;
    private Integer numberAnswer;
    private String answerType;


}