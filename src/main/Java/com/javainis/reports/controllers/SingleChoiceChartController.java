package com.javainis.reports.controllers;

import com.javainis.reports.api.SingleChoiceQuestionReport;
import com.javainis.reports.mybatis.model.*;
import lombok.Getter;
import org.apache.deltaspike.core.api.future.Futureable;
import org.primefaces.model.chart.PieChartModel;

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

    @Getter
    private PieChartModel model;

    //@PostConstruct
    public void init() {
        createPieModel();
    }

    private void createPieModel() {
        model = new PieChartModel();

        for(Choice q: singleChoiceQuestion.getChoices())
        {
            int count = 0;
            for(SingleChoiceAnswer a: singleChoiceAnswers)
                if(a.toString() == q.getText())
                    count++;

            model.set(q.toString(), count);
        }


        model.setTitle(singleChoiceQuestion.getText());
        model.setLegendPosition("w");
        model.setShowDataLabels(true);
    }

    @Override
    public String getTemplateName() {
        return "single-choice-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof SingleChoiceQuestion) {
            singleChoiceQuestion = (SingleChoiceQuestion) question;
            //for(Answer a: singleChoiceQuestion.getAnswers())
            singleChoiceAnswers = (List<SingleChoiceAnswer>) (List<?>) singleChoiceQuestion.getAnswers();
        }
        else {
            System.out.println("SingleChoiceQuestion was not set successfully");
        }
    }

    @Override
    @Futureable
    public Future<Void> generateReportAsync() {
        init();
        return new AsyncResult<>(null);
    }
}
