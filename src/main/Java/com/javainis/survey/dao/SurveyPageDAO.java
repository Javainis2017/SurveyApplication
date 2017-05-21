package com.javainis.survey.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class SurveyPageDAO {
    @Inject
    private EntityManager manager;

    public void deleteBySurveyId(Long surveyId){
        manager.createNamedQuery("SurveyPage.deleteBySurveyId").setParameter("surveyId", surveyId).executeUpdate();
    }
}
