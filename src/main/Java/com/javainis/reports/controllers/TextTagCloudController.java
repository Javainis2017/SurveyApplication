package com.javainis.reports.controllers;

import com.javainis.reports.api.TextQuestionReport;
import com.javainis.reports.mybatis.model.Question;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Alternative
@Dependent
public class TextTagCloudController implements TextQuestionReport, Serializable {
    @Override
    public String getTemplateName() {
        return "text-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {

    }

    private Map<String, Integer> findMostFrequentWords(List<String> texts)
    {
        Map<String, Integer> resultMap = new HashMap<>();
        Integer count;
        StringBuilder string = new StringBuilder();
        for(String text: texts)
        {
            string.append(text).append(" ");
        }
        string = new StringBuilder(string.toString().toLowerCase());
        List<String> words =  Arrays.asList(string.toString().split("[^a-zA-Z0-9']+"));
        for(String word: words)
        {
            resultMap.merge(word, 1, (a, b) -> a + b);
        }
        return resultMap;
    }
}
