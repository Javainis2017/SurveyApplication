package com.javainis.survey.dao;

import com.javainis.survey.entities.Condition;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class ConditionDAO {
    @Inject
    protected EntityManager manager;

    public void create(Condition condition) {
        manager.persist(condition);
    }

    public void delete(Condition condition) {
        manager.flush();
        Condition removable = manager.merge(condition);
        System.out.println("Success!");
        manager.remove(removable);
    }

    public void deleteBySurveyId(Long surveyId) {
        manager.createNamedQuery("Condition.deleteBySurveyId").setParameter("surveyId", surveyId).executeUpdate();
    }
}
