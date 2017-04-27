package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.data_converters.XLSXDataExporter;
import com.javainis.data_export_import.interfaces.DataExporter;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Survey;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

@Named
@RequestScoped
public class SurveyExportController {

    @Inject
    private DataExporter exporter;

    @Inject
    private SurveyDAO surveyDAO;

    @Inject
    private SurveyResultDAO surveyResultDAO;

    @Getter
    @Setter
    private Survey selectedSurvey;

    @Getter
    @Setter
    private List<Answer> surveyAnswers;

    @Transactional
    public void exportSurvey()
    {
        Survey test1 = surveyDAO.getAll().get(0);
        exporter.exportSurvey(test1, new File("survey.xlsx"));

    }

    public void exportAnswers()
    {

    }
}
