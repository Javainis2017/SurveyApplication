package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@DiscriminatorValue("C")
@Getter
@Setter
public class SingleChoiceAnswer extends Answer{

    @JoinColumn(name = "choice_id", referencedColumnName = "id")
    @ManyToOne()
    private Choice choice;

    @Override
    public boolean hasAnswer() {
        return choice != null;
    }
}

