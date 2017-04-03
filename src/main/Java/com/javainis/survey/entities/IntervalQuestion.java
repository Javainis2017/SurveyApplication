package com.javainis.survey.entities;

import javax.persistence.Entity;

@Entity
public class IntervalQuestion extends Question{
    private String questionText;
    private Boolean required;
}
