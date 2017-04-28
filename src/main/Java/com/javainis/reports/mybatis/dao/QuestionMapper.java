package com.javainis.reports.mybatis.dao;

import com.javainis.reports.mybatis.model.Question;
import org.mybatis.cdi.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {

    List<Question> selectBySurveyId(Long id);
}