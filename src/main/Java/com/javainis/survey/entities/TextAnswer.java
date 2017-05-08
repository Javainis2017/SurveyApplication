package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("T")
@Getter
@Setter
public class TextAnswer extends Answer{

    @Column(name = "text_answer")
    private String text;

    @Override
    public boolean hasAnswer() {
        return !text.isEmpty();
    }
}
