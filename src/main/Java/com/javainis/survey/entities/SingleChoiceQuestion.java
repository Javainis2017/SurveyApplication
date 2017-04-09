package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity

@Getter
@Setter
@DiscriminatorValue("2")
public class SingleChoiceQuestion extends Question{

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Choice> answers = new ArrayList<>();
}
