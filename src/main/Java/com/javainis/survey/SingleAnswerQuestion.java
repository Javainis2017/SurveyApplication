package com.javainis.survey;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class SingleAnswerQuestion extends Question{
    private String questionText;
    private Boolean required;
    @OneToMany
    private List<Choice> answers;
}
