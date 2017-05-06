package com.javainis.survey.dao;

import com.javainis.survey.entities.Answer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class AnswerDAO {
    @Inject
    private EntityManager manager;

    public void create(Answer answer) {
        manager.persist(answer);
    }
    public void delete(Answer answer) {manager.remove(answer);}

    public List<Answer> findByQuestionId(Long questionId){
        return manager.createNamedQuery("Answer.findByQuestionId", Answer.class).setParameter("questionId", questionId).getResultList();
    }
}
