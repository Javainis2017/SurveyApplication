package com.javainis.survey.entities;

import com.javainis.user_management.entities.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey")
@NamedQueries({
    @NamedQuery(name = "Survey.findAll", query = "SELECT s FROM Survey s"),
    @NamedQuery(name = "Survey.findById", query = "SELECT s FROM Survey s WHERE s.id = :id"),
    @NamedQuery(name = "Survey.findByUrl", query = "SELECT s FROM Survey s WHERE s.url = :url"),
    @NamedQuery(name = "Survey.findByAuthorId", query = "SELECT s FROM Survey s WHERE s.author.userID = :authorId")
})
@Getter
@Setter

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

    @Column(name = "url")
    @Size(max = 32, min = 32)
    private String url;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User author;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

}
