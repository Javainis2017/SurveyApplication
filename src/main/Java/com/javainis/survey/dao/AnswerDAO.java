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

    public void deleteByQuestionId(Long questionId){
        manager.createNativeQuery("DELETE FROM answer_choice USING answer a WHERE answer_id = a.id AND a.question_id = :questionId").setParameter("questionId", questionId).executeUpdate();
        manager.createNamedQuery("Answer.deleteByQuestionId").setParameter("questionId", questionId).executeUpdate();
    }

    public List<Answer> findByQuestionId(Long questionId){
        return manager.createNamedQuery("Answer.findByQuestionId", Answer.class).setParameter("questionId", questionId).getResultList();
    }
}
