package com.javainis.reports.controllers;

import com.javainis.reports.api.TextQuestionReport;
import com.javainis.reports.mybatis.model.FreeTextQuestion;
import com.javainis.reports.mybatis.model.Question;
import com.javainis.reports.mybatis.model.TextAnswer;
import lombok.Getter;
import lombok.Setter;
import org.apache.deltaspike.core.api.future.Futureable;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

import javax.ejb.AsyncResult;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;

@Named
@Dependent
@Alternative
public class TextSimpleController implements TextQuestionReport, Serializable {
    @Getter
    FreeTextQuestion freeTextQuestion;
    @Getter
    List<TextAnswer> textAnswers;

    @Override
    public String getTemplateName() {
        return "text-simple-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof FreeTextQuestion) {
            freeTextQuestion = (FreeTextQuestion) question;
            textAnswers = (List<TextAnswer>) (List<?>) freeTextQuestion.getAnswers();
        }
        else {
            System.out.println("FreeTextQuestion was not set successfully");
        }
    }

    @Override
    @Futureable
    public Future<Void> generateReportAsync() {
        return new AsyncResult<>(null);
    }

}
