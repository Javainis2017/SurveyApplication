package com.javainis.data_export_import.interfaces;

import com.javainis.survey.entities.Survey;

import java.io.OutputStream;
import java.util.concurrent.Future;

public interface DataExporter {

    Future<Void> exportSurvey(Survey survey, OutputStream destination);

}
