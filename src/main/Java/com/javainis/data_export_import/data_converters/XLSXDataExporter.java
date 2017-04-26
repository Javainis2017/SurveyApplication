package com.javainis.data_export_import.data_converters;

import com.javainis.data_export_import.interfaces.DataExporter;
import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Survey;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.List;

@ApplicationScoped
public class XLSXDataExporter implements DataExporter {

    private File file;

    @Override
    public void exportSurvey(Survey survey, File destination) {

    }

    @Override
    public void exportAnswers(List<Answer> answers, File destination) {

    }
}
