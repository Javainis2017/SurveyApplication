package com.javainis.survey.dao;

import com.javainis.survey.entities.Survey;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class SurveyDAO {

    @Inject
    private EntityManager manager;

    public void create(Survey survey) {
        manager.persist(survey);
    }

    public List<Survey> getAll(String url){
        return manager.createNamedQuery("Survey.findAll", Survey.class).getResultList();
    }

    public Survey findById(Long id){
        return manager.createNamedQuery("Survey.findById", Survey.class).getSingleResult();
    }
    public List<Survey> findByAuthorId(Long authorId){
        return manager.createNamedQuery("Survey.findByAuthorId", Survey.class).setParameter("authorId", authorId).getResultList();
    }
    public Survey findByUrl(String url){
        return manager.createNamedQuery("Survey.findByUrl", Survey.class).setParameter("url", url).getSingleResult();
    }

    public boolean existsByUrl(String url){
        return manager.createNamedQuery("Survey.existsByUrl", Long.class).setParameter("url", url).getSingleResult() > 0;
    }
}
