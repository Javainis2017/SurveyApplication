package com.javainis.survey.controllers.show;


import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Survey;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

@Named
@RequestScoped
public class SurveyController {

    @Inject
    private SurveyDAO surveyDAO;

    @Setter
    @Getter
    private String surveyUrl;

    @Getter
    private Survey survey;

    public void init(){
        // Check if parameter exists
        if(surveyUrl == null){
            return;
        }

        // Find survey
        try{
            survey = surveyDAO.findByUrl(surveyUrl);
        }catch (NoResultException ex){
            return;
        }
    }
}
