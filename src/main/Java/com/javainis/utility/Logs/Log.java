package com.javainis.utility.Logs;

import com.javainis.survey.entities.Survey;
import com.javainis.user_management.entities.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "logs")
@Getter
@Setter
public class Log
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long LogID;

    @Size(max = 70)
    @Column(name = "user_name")
    private String userName;

    @Size(max = 80)
    @Column(name = "survey_name")
    private String surveyName;

    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne
    private Survey survey;

    @Size(max = 10)
    @Column(name = "rights")
    private String rights;

    @Column(name = "time")
    private Timestamp time;

    @Size(max = 100)
    @Column(name = "class_method")
    private String class_method;
}
