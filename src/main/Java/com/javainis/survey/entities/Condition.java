package com.javainis.survey.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity

@Getter
@Setter
@Table(name="condition")
@EqualsAndHashCode(of = {"dependentQuestion", "question", "choice"})
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne
    private Question question; //Question which fields are checked

    @JoinColumn(name = "dependent_question_id", referencedColumnName = "id")
    @ManyToOne
    private Question dependentQuestion; //Question with condition

    @JoinColumn(name = "choice_id", referencedColumnName = "id")
    @ManyToOne
    private Choice choice;

    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne
    private Survey survey;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;
}
