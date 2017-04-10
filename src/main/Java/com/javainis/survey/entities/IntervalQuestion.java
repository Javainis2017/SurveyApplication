package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity

@Getter
@Setter
@DiscriminatorValue("4")

@Table(name = "interval_question")
public class IntervalQuestion extends Question{

    @Column(name = "min_value")
    private int minValue;

    @Column(name = "max_value")
    private int maxValue;
}
