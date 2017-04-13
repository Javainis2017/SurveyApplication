package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_result")
@Getter
@Setter
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @ManyToOne()
    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    private Survey survey;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

}
