package com.javainis.data_export_import.controllers;

import com.javainis.data_export_import.interfaces.DataExporter;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.Answer;
import com.javainis.survey.entities.Survey;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.context.RequestContext;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private SurveyDAO surveyDAO;

    @Getter
    @Setter
    private Survey selectedSurvey;

    @Getter
    private boolean timeout = false;

    @Getter
    private Map<String, File> generatedSurveys = new HashMap<>();

    @Transactional
    public void exportSurvey(Survey survey)
    {
        selectedSurvey = survey;

//        Pagal http://stackoverflow.com/a/9394237
//        FacesContext fc = FacesContext.getCurrentInstance();
//        ExternalContext ec = fc.getExternalContext();
        Messages.addGlobalInfo("Generating file, please wait...");
        try {
            file = new File(selectedSurvey.getUrl() + ".xlsx");
            stream = new FileOutputStream(file);
//            stream = ec.getResponseOutputStream();
//            export = exporter.exportSurvey(selectedSurvey, stream);
            exporter.exportSurvey(selectedSurvey, stream);
            generatedSurveys.put(selectedSurvey.getUrl(), file);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            Messages.addGlobalWarn("Could not generate file, error: " + ex.getMessage());
        }
    }

    public void checkProgress() {
        if(file != null && file.length() > 1)
        {
            timeout = true;
        }
    }

    public void downloadToUser(String surveyURL)
    {
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
