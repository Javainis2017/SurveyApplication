package com.javainis.reports.mybatis.dao;

import com.javainis.reports.mybatis.model.SurveyResult;
import java.util.List;

public interface SurveyResultMapper {

    int deleteByPrimaryKey(Long id);
    int insert(SurveyResult record);
    SurveyResult selectByPrimaryKey(Long id);
    List<SurveyResult> selectAll();
    int updateByPrimaryKey(SurveyResult record);
}