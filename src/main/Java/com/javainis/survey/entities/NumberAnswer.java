package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("N")
@Getter
@Setter
public class NumberAnswer extends Answer{
    
    @Column(name = "number_answer")
    private Integer number;
}
