package com.javainis.survey.dao;

import com.javainis.survey.entities.Question;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.awt.*;
import java.util.List;

public class ChoiceDAO {
    @Inject
    protected EntityManager manager;

    public void create(Choice choice) {
        manager.persist(choice);
    }

    public List<Choice> getAll(){
        return manager.createNamedQuery("Choice.findAll", Choice.class).getResultList();
    }

    public Choice findById(Long id){
        return manager.createNamedQuery("Choice.findById", Choice.class).getSingleResult();
    }
    public List<Choice> findByQuestionId(Long questionId){
        return manager.createNamedQuery("Choice.findByQuestionId", Choice.class).setParameter("questionId", questionId).getResultList();
    }
}
