package com.javainis.reports.mybatis.dao;

import com.javainis.reports.mybatis.model.Survey;
import org.mybatis.cdi.Mapper;

import java.util.List;

@Mapper
public interface SurveyMapper {

    Survey selectByUrl(String url);
}