package com.javainis.data_export_import.interfaces;

import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Survey;

import java.io.File;
import java.util.List;

public interface DataExporter {

    void exportSurvey(Survey survey, File destination);
    void exportAnswers(List<Answer> answers, File destination);

}
