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
@DiscriminatorValue("3")
public class MultipleChoiceQuestion extends Question{

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Choice> choices = new ArrayList<>();
}
