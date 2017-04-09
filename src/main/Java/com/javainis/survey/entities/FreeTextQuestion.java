package com.javainis.survey.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "free_text_question")
@DiscriminatorValue("1")
public class FreeTextQuestion extends Question{

}
