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
        @NamedQuery(name = "Question.findBySurveyId", query = "SELECT q FROM Question q WHERE q.survey.id = :surveyId ORDER BY q.position")
})
@DiscriminatorColumn(name="question_type_id", discriminatorType = DiscriminatorType.INTEGER)
@EqualsAndHashCode(of = "text")
public abstract class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 300)
    @Column(name = "text")
    private String text;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "position")
    private Integer position;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne
    private Survey survey;

    @JoinColumn(name = "page_id", referencedColumnName = "id")
    @ManyToOne
    private SurveyPage page;

}
