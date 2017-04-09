package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity

@Getter
@Setter
@DiscriminatorValue("2")
@Table(name = "single_choice_question")
public class SingleChoiceQuestion extends Question{

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Choice> answers = new ArrayList<>();
}
