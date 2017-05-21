package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_result")
@NamedQueries({
        @NamedQuery(name = "SurveyResult.findBySurveyId", query = "SELECT sr FROM SurveyResult sr WHERE sr.survey.id = :surveyId"),
        @NamedQuery(name = "SurveyResult.findByUrl", query = "SELECT s FROM SurveyResult s WHERE s.url = :url"),
        @NamedQuery(name = "SurveyResult.existsByUrl", query = "SELECT COUNT(s) FROM SurveyResult s WHERE s.url = :url "),
        @NamedQuery(name = "SurveyResult.existsById", query = "SELECT COUNT(s) FROM SurveyResult s WHERE s.id = :id")
})
@Getter
@Setter
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "complete")
    private boolean complete;

    @Column(name = "url")
    @Size(max = 32, min = 32)
    private String url;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @ManyToOne()
    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    private Survey survey;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

}
