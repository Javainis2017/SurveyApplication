package com.javainis.survey.dao;

import com.javainis.survey.entities.SurveyResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@ApplicationScoped
public class SurveyResultDAO {

    @Inject
    private EntityManager manager;

    public void create(SurveyResult surveyResult) {
        manager.persist(surveyResult);
    }

    public void update(SurveyResult surveyResult){
        SurveyResult mergedSurvey = manager.merge(surveyResult);
        manager.lock(mergedSurvey, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        manager.flush();
    }

    public List<SurveyResult> getResultsBySurveyId(Long surveyId){
        return manager.createNamedQuery("SurveyResult.findBySurveyId", SurveyResult.class).setParameter("surveyId", surveyId).getResultList();
    }

    public SurveyResult findByUrl(String surveyUrl) {
        return manager.createNamedQuery("SurveyResult.findByUrl", SurveyResult.class).setParameter("url", surveyUrl).getSingleResult();
    }

    public boolean existsByUrl(String genUrl) {
        return manager.createNamedQuery("SurveyResult.existsByUrl", Long.class).setParameter("url", genUrl).getSingleResult() > 0;
    }

    public boolean existsById(Long id) {
        return manager.createNamedQuery("SurveyResult.existsById", Long.class).setParameter("id", id).getSingleResult() > 0;
    }
}
