package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("C")
@Getter
@Setter
public class ChoiceAnswer extends Answer{

    @JoinTable(name = "answer_choice", joinColumns = {
        @JoinColumn(name = "answer_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "choice_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Choice> choices = new ArrayList<>();
}
