package com.javainis.reports.controllers;

import com.javainis.reports.api.SingleChoiceQuestionReport;
import com.javainis.reports.mybatis.model.SingleChoiceQuestion;
import com.javainis.reports.mybatis.model.Question;
import com.javainis.reports.mybatis.model.SingleChoiceAnswer;
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
@Dependent
@Alternative
public class SingleChoiceChartController implements SingleChoiceQuestionReport, Serializable {

    @Getter
    SingleChoiceQuestion singleChoiceQuestion;
    @Getter
    List<SingleChoiceAnswer> singleChoiceAnswers;

    @Override
    public String getTemplateName() {
        return "single-choice-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof SingleChoiceQuestion) {
            singleChoiceQuestion = (SingleChoiceQuestion) question;
            singleChoiceAnswers = (List<SingleChoiceAnswer>) (List<?>) singleChoiceQuestion.getAnswers();
        }
        else {
            System.out.println("SingleChoiceQuestion was not set successfully");
        }
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
