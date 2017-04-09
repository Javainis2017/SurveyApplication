package com.javainis.survey.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class FreeTextQuestion extends Question{

}
