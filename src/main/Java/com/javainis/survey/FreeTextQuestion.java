package com.javainis.survey;

import javax.persistence.Entity;

@Entity
public class FreeTextQuestion extends Question{
    private String questionText;
    private Boolean required;
}
