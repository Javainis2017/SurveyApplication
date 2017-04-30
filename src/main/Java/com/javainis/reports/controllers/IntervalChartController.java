package com.javainis.reports.controllers;

import com.javainis.reports.api.IntervalQuestionReport;
import com.javainis.reports.mybatis.model.IntervalQuestion;
import com.javainis.reports.mybatis.model.NumberAnswer;
import com.javainis.reports.mybatis.model.Question;
import lombok.Getter;
import org.apache.deltaspike.core.api.future.Futureable;

import javax.ejb.AsyncResult;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

@Named
@Alternative
@Dependent
public class IntervalChartController implements IntervalQuestionReport, Serializable {

    @Getter
    IntervalQuestion question;
    @Getter
    List<NumberAnswer> answers;

    @Override
    public String getTemplateName() {
        return "interval-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        this.question = (IntervalQuestion) question;
    }

    @Override
    @Futureable
    public Future<Void> generateReportAsync() {
        // TODO: REMOVE
        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){

        }
        return new AsyncResult<>(null);
    }
}
