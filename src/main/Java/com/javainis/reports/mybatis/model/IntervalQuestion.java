package com.javainis.reports.mybatis.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntervalQuestion extends Question{
    private Integer min;
    private Integer max;
}
