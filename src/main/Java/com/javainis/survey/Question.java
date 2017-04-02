package com.javainis.survey;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public abstract class Question {
    @Id
    private int id;
}
