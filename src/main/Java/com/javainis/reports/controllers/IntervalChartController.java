package com.javainis.reports.controllers;

import com.javainis.reports.api.IntervalQuestionReport;
import com.javainis.reports.mybatis.model.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.deltaspike.core.api.future.Futureable;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import javax.ejb.AsyncResult;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
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
    List<Integer> numbers;
    @Getter
    double average;
    @Getter
    int mode;
    @Getter
    double median;
    @Getter
    int percentile25;
    @Getter
    int percentile75;
    @Getter
    private BarChartModel barModel;

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

    public void countStatistics() {
        getSortedNumbers();
        countAverage();
        countMode();
        countMedian();
        percentile25 = countPercentiles(25);
        percentile75 = countPercentiles(75);
    }

    private int countPercentiles(int percent) {
        double indexValue = (double)(numbers.size()*percent)/100;
        return numbers.get((int)Math.ceil(indexValue)-1);
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
        int min = intervalQuestion.getMin();
        int max = intervalQuestion.getMax();
        int difference = max-min+1;
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
        int size = numbers.size();
        int middle = size/2;
        if (size%2 == 1) {
            median = numbers.get(middle);
        } else {
            median = (double)(numbers.get(middle-1) + numbers.get(middle)) / 2.0;
        }
    }

    private void getSortedNumbers(){
        numbers = new ArrayList<>();
        for(NumberAnswer answer: numberAnswers)
        {
            numbers.add(answer.getNumber());
        }
        Collections.sort(numbers);
    }
    @Override
    @Futureable
    public Future<Void> generateReportAsync() {
        // TODO: REMOVE
        countStatistics();
        fillChart();
        return new AsyncResult<>(null);
    }

    public void fillChart() {
        Map<Integer, Integer> valueCount = new TreeMap<>();
        int groupFrom = 20;
        int max = intervalQuestion.getMax();
        int min = intervalQuestion.getMin();
        for(NumberAnswer answer: numberAnswers){
            valueCount.merge(answer.getNumber(), 1, (a, b) -> a + b);
        }
        barModel = new BarChartModel();
        ChartSeries values = new ChartSeries();
        values.setLabel("Counts");
        if(max-min>groupFrom)
        {
            int counter = -1;
            int from = min;
            int to;
            int sum = 0;
            for (int i = min; i <= max; i++) {
                if(counter == (max-min)/10){
                    to = i-1;
                    values.set(from+" - "+to,sum);
                    counter = 0;
                    from = i;
                    sum = 0;
                }
                if (valueCount.containsKey(i)) {
                    sum+=valueCount.get(i);
                }
                counter++;
            }
            if(counter!=0){
                to = max;
                values.set(from+" - "+to, sum);
            }
        }
        else {
            for (int i = min; i <= max; i++) {
                if (valueCount.containsKey(i)) {
                    values.set(i, valueCount.get(i));
                } else {
                    values.set(i, 0);
                }
            }
        }
        barModel.addSeries(values);
//        barModel.setTitle("Interval values Bar Chart");
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Values");
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Count");
    }
}
