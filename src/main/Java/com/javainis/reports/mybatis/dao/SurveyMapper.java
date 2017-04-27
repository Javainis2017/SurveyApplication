package com.javainis.reports.mybatis.dao;

import com.javainis.reports.mybatis.model.Survey;
import java.util.List;

public interface SurveyMapper {

    int deleteByPrimaryKey(Long id);
    int insert(Survey record);
    Survey selectByPrimaryKey(Long id);
    List<Survey> selectAll();
    int updateByPrimaryKey(Survey record);
}