package com.javainis.data_export_import.interfaces;

import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Survey;
import com.javainis.survey.entities.SurveyResult;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Future;

public interface DataExporter {

    void exportSurvey(Survey survey, OutputStream destination);
    void exportAnswers(List<SurveyResult> answers, OutputStream destination);

}
