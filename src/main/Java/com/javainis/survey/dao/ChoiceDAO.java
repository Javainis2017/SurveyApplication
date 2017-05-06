package com.javainis.survey.dao;

import com.javainis.survey.entities.Choice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
@ApplicationScoped
public class ChoiceDAO {
    @Inject
    private EntityManager manager;

    public void create(Choice choice) {
        manager.persist(choice);
    }

    public void delete(Choice choice) {
        manager.flush();
        manager.remove(choice);
    }

    public List<Choice> getAll(){
        return manager.createNamedQuery("Choice.findAll", Choice.class).getResultList();
    }

    public Choice findById(Long id){
        return manager.createNamedQuery("Choice.findById", Choice.class).setParameter("id", id).getSingleResult();
    }
    public List<Choice> findByQuestionId(Long questionId){
        return manager.createNamedQuery("Choice.findByQuestionId", Choice.class).setParameter("questionId", questionId).getResultList();
    }
}
