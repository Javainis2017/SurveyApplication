package com.javainis.survey.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_page")
@Getter
@Setter
@EqualsAndHashCode(of = {"number", "survey"})
public class SurveyPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private Integer number;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne
    private Survey survey;

    @OneToMany(mappedBy = "page")
    @OrderBy("position ASC")
    private List<Question> questions = new ArrayList<>();
}
