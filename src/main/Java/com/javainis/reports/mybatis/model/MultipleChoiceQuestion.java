package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultipleChoiceQuestion extends Question{
    private List<Choice> choices;
}
