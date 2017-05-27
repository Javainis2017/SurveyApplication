package com.javainis.survey.dao;

import com.javainis.Async;
import com.javainis.survey.entities.Survey;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@ApplicationScoped
public class SurveyAsyncDAO {

    @Inject @Async
    private EntityManager manager;

    public void create(Survey survey) {
        manager.persist(survey);
    }

    public void update(Survey survey){
        Survey mergedSurvey = manager.merge(survey);
        manager.lock(mergedSurvey, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        manager.flush();
    }

    public void delete(Survey survey) {
        manager.flush();
        manager.remove(survey);
    }

    public List<Survey> getAll(){
        return manager.createNamedQuery("Survey.findAll", Survey.class).getResultList();
    }

    public Survey findById(Long id){
        return manager.createNamedQuery("Survey.findById", Survey.class).setParameter("id", id).getSingleResult();
    }

    public boolean existsByUrl(String url){
        return manager.createNamedQuery("Survey.existsByUrl", Long.class).setParameter("url", url).getSingleResult() > 0;
    }
}
