package com.javainis.reports.controllers;

import com.javainis.reports.api.IntervalQuestionReport;
import com.javainis.reports.mybatis.model.*;
import lombok.Getter;
import org.apache.deltaspike.core.api.future.Futureable;

import javax.ejb.AsyncResult;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

@Named
@Alternative
@Dependent
public class IntervalChartController implements IntervalQuestionReport, Serializable {
    @Getter
    IntervalQuestion intervalQuestion;
    @Getter
    List<NumberAnswer> numberAnswers;
    @Getter
    double average;
    @Getter
    int mode;
    @Getter
    double median;
    @Override
    public String getTemplateName() {
        return "interval-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof IntervalQuestion) {
            intervalQuestion = (IntervalQuestion) question;
            numberAnswers = (List<NumberAnswer>) (List<?>) intervalQuestion.getAnswers();
        }
        else {
            System.out.println("IntervalQuestion was not set successfully");
        }
    }
    public void countAverage(){
        double sum = 0;
        for(NumberAnswer number:numberAnswers)
        {
            sum+=number.getNumber();
        }
        average = sum/numberAnswers.size();
    }

    public void countMode(){
        List<Integer> numbers = getSortedNumbers();
        int min = intervalQuestion.getMin();
        int max = intervalQuestion.getMax();
        int difference = max-min;
        int occurrences[] = new int[difference];
        int maxValue = Integer.MIN_VALUE;
        int maxCount = 0;
        for(int number:numbers)
        {
            occurrences[number-min]++;
            if(occurrences[number-min] > maxCount)
            {
                maxCount = occurrences[number-min];
                maxValue = number;
            }
        }
        if(maxValue == Integer.MIN_VALUE && maxCount == 0) {
            System.out.println("Mode was not found");
        }
        else{
            mode = maxValue;
        }
    }

    public void countMedian(){
        List<Integer> numbers = getSortedNumbers();
        int size = numbers.size();
        int middle = size/2;
        if (size%2 == 1) {
            median = numbers.get(middle);
        } else {
            median = (double)(numbers.get(middle-1) + numbers.get(middle)) / 2.0;
        }
    }

    private List<Integer> getSortedNumbers(){
        List<Integer> numbers = new ArrayList<>();
        for(NumberAnswer answer: numberAnswers)
        {
            numbers.add(answer.getNumber());
        }
        Collections.sort(numbers);
        return numbers;
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
