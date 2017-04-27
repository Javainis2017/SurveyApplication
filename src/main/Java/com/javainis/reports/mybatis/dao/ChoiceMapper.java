package com.javainis.reports.mybatis.dao;

import com.javainis.reports.mybatis.model.Choice;
import java.util.List;

public interface ChoiceMapper {

    int deleteByPrimaryKey(Long id);
    int insert(Choice record);
    Choice selectByPrimaryKey(Long id);
    List<Choice> selectAll();
    int updateByPrimaryKey(Choice record);
}