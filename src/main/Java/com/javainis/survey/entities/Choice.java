package com.javainis.survey.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NamedQueries({
        @NamedQuery(name = "Choice.findAll", query = "SELECT c FROM Choice c"),
        @NamedQuery(name = "Choice.findById", query = "SELECT c FROM Choice c WHERE c.id = :id"),
        @NamedQuery(name = "Choice.findByQuestionId", query = "SELECT c FROM Choice c WHERE c.question.id = :questionId")
})
@Table(name="choice")
@EqualsAndHashCode(of = {"text"})
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne
    private Question question;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @Override
    public String toString() {
        return "Choice{" +
                "id=" + id + '}';
    }
}
