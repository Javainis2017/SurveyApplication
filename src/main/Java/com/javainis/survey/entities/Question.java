package com.javainis.survey.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public abstract class Question {
    @Id
    private int id;
}
