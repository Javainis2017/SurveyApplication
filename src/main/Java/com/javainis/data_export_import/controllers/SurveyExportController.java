package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.interfaces.DataExporter;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.Survey;
import com.javainis.survey.entities.SurveyResult;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Named
@ViewScoped
public class SurveyExportController implements Serializable {

    private OutputStream stream;

    private File file;

    @Inject
    private DataExporter exporter;

    @Inject
    private SurveyResultDAO resultDAO;

    @Inject
    private SurveyDAO surveyDAO;

    @Getter
    @Setter
    private Survey selectedSurvey;

    @Getter
    private boolean timeout = false;

    @Getter
    private Future<Void> export;
    @Getter
    private Map<String, File> generatedSurveys = new HashMap<>();

    private Map<Future<Void>, Pair<String, File>> generatingNow = new HashMap<>();

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void exportSurvey(Survey survey)
    {
        selectedSurvey = survey;
        if(generatedSurveys.containsKey(selectedSurvey.getUrl()))
        {
            generatedSurveys.remove(selectedSurvey.getUrl());
        }
        Messages.addGlobalInfo("Generating file, please wait...");
        try {
            String name = selectedSurvey.getTitle().replace('/',' ');
            file = new File(name + "_" + selectedSurvey.getUrl() + ".xlsx");
            stream = new FileOutputStream(file);
            List<SurveyResult> results = resultDAO.getResultsBySurveyId(selectedSurvey.getId());
            if (results.size() > 0){
                Hibernate.initialize(results);
            }
            initCollections(selectedSurvey, results);
            export = exporter.exportSurvey(selectedSurvey, stream);
            generatingNow.put(export, new Pair<>(selectedSurvey.getUrl(), file));
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            Messages.addGlobalWarn("ERROR: Failed to generate file");
        }
    }

    //Be Hibernate.initialize meta LazyInitializationException
    private void initCollections(Survey survey, List<SurveyResult> results){
        Hibernate.initialize(survey);
        Hibernate.initialize(survey.getQuestions());
        if (results.size() > 0){
            Hibernate.initialize(results);
            for(SurveyResult res: results){
                Hibernate.initialize(res.getAnswers());
            }
        }

    }

    public void checkProgress() {
        for(Future<Void> export : generatingNow.keySet()) {
            if(export != null && export.isDone()) {
                timeout = true;
                Pair<String, File> result = generatingNow.get(export);
                generatedSurveys.put(result.getKey(), result.getValue());
            } else {
                timeout = false;
                break;
            }
        }
    }

    public void downloadToUser(String surveyURL) {
        if (generatedSurveys.containsKey(surveyURL)) {
            file = generatedSurveys.get(surveyURL);
            try {
                Faces.sendFile(file, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                file = null;
                selectedSurvey = null;
            }
        }
    }
}
