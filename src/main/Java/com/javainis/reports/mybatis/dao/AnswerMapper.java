package com.javainis.reports.mybatis.dao;

import com.javainis.reports.mybatis.model.Answer;
import java.util.List;

public interface AnswerMapper {

    int deleteByPrimaryKey(Long id);
    int insert(Answer record);
    Answer selectByPrimaryKey(Long id);
    List<Answer> selectAll();
    int updateByPrimaryKey(Answer record);
}