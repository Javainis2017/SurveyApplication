package com.javainis.survey.entities;

import com.javainis.user_management.entities.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey")
@NamedQueries({
    @NamedQuery(name = "Survey.findAll", query = "SELECT s FROM Survey s"),
    @NamedQuery(name = "Survey.findById", query = "SELECT s FROM Survey s WHERE s.id = :id"),
    @NamedQuery(name = "Survey.findByUrl", query = "SELECT s FROM Survey s WHERE s.url = :url"),
    @NamedQuery(name = "Survey.findByAuthorId", query = "SELECT s FROM Survey s WHERE s.author.userID = :authorId"),
    @NamedQuery(name = "Survey.existsByUrl", query = "SELECT COUNT(s) FROM Survey s WHERE s.url = :url ")
})
@Getter
@Setter
@EqualsAndHashCode(of = {"title", "description", "questions", "author"})
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @Size(max = 80)
    private String title;

    @Column(name = "description")
    @Size(max = 500)
    private String description;

    @Column(name = "expiration_time")
    private Timestamp expirationTime;

    @Column(name = "url")
    @Size(max = 32, min = 32)
    private String url;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User author;

    @Column(name = "public")
    private Boolean isPublic;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<SurveyResult> surveyResults = new ArrayList<>();
}
