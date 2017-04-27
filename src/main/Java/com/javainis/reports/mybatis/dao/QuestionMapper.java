package com.javainis.reports.mybatis.dao;

import com.javainis.reports.mybatis.model.Question;
import java.util.List;

public interface QuestionMapper {

    int deleteByPrimaryKey(Long id);
    int insert(Question record);
    Question selectByPrimaryKey(Long id);
    List<Question> selectAll();
    int updateByPrimaryKey(Question record);
}