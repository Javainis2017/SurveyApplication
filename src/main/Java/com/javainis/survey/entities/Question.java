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
    @Column(name = "id")
    private int id;

    @Size(max = 300)
    @Column(name = "text")
    private String text;

    @Column(name = "required")
    private Boolean required;

    @JoinColumn(name = "question_type_id", referencedColumnName = "id")
    @ManyToOne
    private QuestionType type;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne
    private Survey survey;

}
