package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.interfaces.DataExporter;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Survey;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.*;
import java.util.List;

@Named
@RequestScoped
public class SurveyExportController {

    private OutputStream stream;

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


        //TIK TESTAVIMUI
        selectedSurvey = surveyDAO.getAll().get(0); //TIK TESTAVIMUI
        //TIK TESTAVIMUI

        //Pagal http://stackoverflow.com/a/9394237
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.responseReset();
        ec.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\" " + selectedSurvey.getTitle() + ".xlsx\"");
        //TODO: padaryt visom apklausom
        try {
            stream = ec.getResponseOutputStream();
            exporter.exportSurvey(selectedSurvey, stream);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
            fc.responseComplete();
        }
    }

}
