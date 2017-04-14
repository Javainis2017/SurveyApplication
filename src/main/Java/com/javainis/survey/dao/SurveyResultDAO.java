package com.javainis.survey.dao;

import com.javainis.survey.entities.SurveyResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class SurveyResultDAO {

    @Inject
    private EntityManager manager;

    public void create(SurveyResult surveyResult) {
        manager.persist(surveyResult);
    }

    public List<SurveyResult> getResultsBySurveyId(Long surveyId){
        return manager.createNamedQuery("SurveyResult.findBySurveyId", SurveyResult.class).setParameter("surveyId", surveyId).getResultList();
    }
}
