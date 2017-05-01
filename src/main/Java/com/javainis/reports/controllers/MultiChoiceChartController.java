package com.javainis.reports.controllers;

import com.javainis.reports.api.MultiChoiceQuestionReport;
import com.javainis.reports.mybatis.model.*;
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
public class MultiChoiceChartController implements MultiChoiceQuestionReport, Serializable {
    @Getter
    MultipleChoiceQuestion multipleChoiceQuestion;
    @Getter
    List<MultipleChoiceAnswer> multipleChoiceAnswers;

    @Override
    public String getTemplateName() {
        return "multi-choice-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof MultipleChoiceQuestion) {
            multipleChoiceQuestion = (MultipleChoiceQuestion) question;
            multipleChoiceAnswers = (List<MultipleChoiceAnswer>) (List<?>) multipleChoiceQuestion.getAnswers();
        }
        else {
            System.out.println("MultipleChoiceQuestion was not set successfully");
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
