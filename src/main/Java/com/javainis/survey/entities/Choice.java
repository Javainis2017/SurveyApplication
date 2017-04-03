package com.javainis.survey.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
class Choice {
    @Id
    private int id;
    private String text;
}
