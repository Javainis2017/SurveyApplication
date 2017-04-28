package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultipleChoiceAnswer extends Answer{

    private List<Long> choiceIds;
}
