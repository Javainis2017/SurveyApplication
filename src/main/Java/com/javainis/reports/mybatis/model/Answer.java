package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Answer {

    private Long id;
    private Long resultId;
    private Long questionId;
}