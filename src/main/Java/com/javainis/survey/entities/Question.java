package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
public abstract class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 300)
    @Column(name = "text")
    private String text;

    @Column(name = "required")
    private Boolean required;

    /*TODO questionType
    @JoinColumn(name = "question_type", referencedColumnName = "id")
    @ManyToOne
    private QuestionType type;*/

    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne
    private Survey survey;

}
