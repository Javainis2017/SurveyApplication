package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "question_type")

@Getter
@Setter
class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @Column(name = "name")
    private String name;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;
}
