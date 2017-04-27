package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Survey {

    private Long id;
    private String name;
    private String description;
    private String url;
    private Long userId;

    private List<Question> questions;

}