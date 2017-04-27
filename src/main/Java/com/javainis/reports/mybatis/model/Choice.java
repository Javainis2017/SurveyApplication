package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Choice {

    private Long id;
    private String text;
    private Long questionId;

    private Question question;
}