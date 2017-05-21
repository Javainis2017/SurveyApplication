package com.javainis.survey.controllers;


import com.javainis.survey.dao.*;
import com.javainis.survey.entities.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class SurveyRemoveController {

    @Inject
    private SurveyDAO surveyDAO;

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    private AnswerDAO answerDAO;

    @Inject
    private ChoiceDAO choiceDAO;

    @Inject
    private ConditionDAO conditionDAO;

    @Inject
    private SurveyPageDAO surveyPageDAO;

    @Inject
    private SurveyResultDAO surveyResultDAO;

    @Transactional
    public String deleteSurvey(Long id){
        List<Question> questions = questionDAO.findBySurveyId(id);

        Survey survey = surveyDAO.findById(id);
        try {
            conditionDAO.deleteBySurveyId(survey.getId());

            for (Question q: questions){
                answerDAO.deleteByQuestionId(q.getId());

                choiceDAO.deleteByQuestionId(q.getId());

                questionDAO.deleteById(q.getId());
            }
            surveyPageDAO.deleteBySurveyId(survey.getId());

            surveyResultDAO.deleteBySurveyId(survey.getId());

            surveyDAO.deleteById(survey.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
