package com.javainis.reports.controllers;

import com.javainis.reports.api.TextQuestionReport;
import com.javainis.reports.mybatis.model.FreeTextQuestion;
import com.javainis.reports.mybatis.model.Question;
import com.javainis.reports.mybatis.model.TextAnswer;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudItem;
import org.primefaces.model.tagcloud.TagCloudModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;

@Named
@Dependent
@Alternative
public class TextTagCloudController implements TextQuestionReport, Serializable {
    @Getter
    FreeTextQuestion freeTextQuestion;
    @Getter
    List<TextAnswer> textAnswers;
    @Getter @Setter
    int wordCount = 30;
    @Getter
    private TagCloudModel model;

    @Override
    public String getTemplateName() {
        return "text-show.xhtml";
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
    public Future generateReportAsync() {
        //TODO
        return null;
    }

    @PostConstruct
    public void init() {
        model = new DefaultTagCloudModel();
        for(Map.Entry<String, Integer> entry : getNTopWords(wordCount).entrySet())
        {
            model.addTag(new DefaultTagCloudItem(entry.getKey(), entry.getValue()));
        }
    }
    public void onSelect(SelectEvent event) {
        TagCloudItem item = (TagCloudItem) event.getObject();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Word Selected", item.getLabel()+" - " + item.getStrength() + " occurrences");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    private Map<String, Integer> findSortedWords(List<String> texts)
    {
        Map<String, Integer> resultMap = new HashMap<>();
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
        resultMap = sortByValue(resultMap);
        return resultMap;
    }
    private Map<String, Integer> getNTopWords(int n)
    {
        List<String> strings = new ArrayList<>();
        for(TextAnswer answer:textAnswers)
        {
            strings.add(answer.getText());
        }
        Map<String, Integer> words = findSortedWords(strings);
        Map<String, Integer> result = new HashMap<>();
        int count = 0;
        for(Map.Entry<String, Integer> entry : words.entrySet())
        {
            if(count >= n)
            {
                break;
            }
            result.put(entry.getKey(), entry.getValue());
            count++;
        }
        return result;
    }
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
