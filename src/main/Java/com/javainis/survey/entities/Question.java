package com.javainis.survey.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "question")
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "Question.findAll", query = "SELECT q FROM Question q"),
        @NamedQuery(name = "Question.findById", query = "SELECT q FROM Question q WHERE q.id = :id"),
        @NamedQuery(name = "Question.findBySurveyId", query = "SELECT q FROM Question q WHERE q.survey.id = :surveyId")
})
@DiscriminatorColumn(name="question_type_id")
@EqualsAndHashCode(of = "text")
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
