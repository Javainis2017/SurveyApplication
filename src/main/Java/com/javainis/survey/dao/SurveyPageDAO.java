package com.javainis.survey.dao;

import com.javainis.survey.entities.SurveyPage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class SurveyPageDAO {
    @Inject
    private EntityManager manager;

    public void create(SurveyPage page){
        manager.persist(page);
    }
    public void deleteBySurveyId(Long surveyId){
        manager.createNamedQuery("SurveyPage.deleteBySurveyId").setParameter("surveyId", surveyId).executeUpdate();
    }
}
