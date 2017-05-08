package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("M")
@Getter
@Setter
public class MultipleChoiceAnswer extends Answer{

    @JoinTable(name = "answer_choice", joinColumns = {
            @JoinColumn(name = "answer_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "choice_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Choice> choices = new ArrayList<>();

    @Override
    public boolean hasAnswer() {
        return !choices.isEmpty();
    }
}
