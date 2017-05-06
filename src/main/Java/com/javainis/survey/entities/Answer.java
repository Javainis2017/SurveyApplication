package com.javainis.survey.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "answer")
@Inheritance
@DiscriminatorColumn(name = "answer_type")
@Getter
@Setter
@NamedQueries({
        @NamedQuery(name = "Answer.findByQuestionId", query = "SELECT a FROM Answer a WHERE a.question.id = :questionId")
})
public abstract class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "result_id", referencedColumnName = "id")
    private SurveyResult result;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;
}
