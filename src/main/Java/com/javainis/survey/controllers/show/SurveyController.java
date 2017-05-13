package com.javainis.survey.controllers.show;

import com.javainis.survey.dao.*;
import com.javainis.survey.entities.*;
import lombok.Getter;
import org.omnifaces.cdi.Param;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class SurveyController implements Serializable{

    @Inject
    private SurveyDAO surveyDAO;

    @Inject
    private SurveyResultDAO surveyResultDAO;

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    private ChoiceDAO choiceDAO;

    @Inject
    private AnswerDAO answerDAO;

    @Inject
    @Param(pathIndex = 0)
    private String surveyUrl;

    @Getter
    private Survey survey;

    @Getter
    private SurveyPage currentPage;

    @Getter
    private Map<Question, Answer> answers = new HashMap<>();

    @PostConstruct
    public void init(){
        // Check if parameter exists
        if(surveyUrl == null){
            return;
        }

        // Find survey
        try{
            survey = surveyDAO.findByUrl(surveyUrl);
        }catch (NoResultException ex){
            return;
        }

        // Init answer objects
        for(Question question : survey.getQuestions()){
            if(question.getClass().getSimpleName().equals("FreeTextQuestion")){
                Answer answer = new TextAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }else if(question.getClass().getSimpleName().equals("IntervalQuestion")){
                Answer answer = new NumberAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }else if(question.getClass().getSimpleName().equals("SingleChoiceQuestion")){
                Answer answer = new SingleChoiceAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }else if(question.getClass().getSimpleName().equals("MultipleChoiceQuestion")){
                Answer answer = new MultipleChoiceAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }
        }
        currentPage = survey.getPages().get(0);
    }

    public void goToPage(int number){
        if(number < 1 || number > survey.getPages().size()){
            return;
        }
        currentPage = survey.getPages().get(number - 1);
    }

    @Transactional
    public String submitAnswers(){
        // Create SurveyResult object
        SurveyResult result = new SurveyResult();
        result.setSurvey(survey);

        // Validation
        List<Answer> answerList = new ArrayList<>(answers.values());
        List<Answer> emptyAnswers = new ArrayList<>();
        for(Answer answer : answerList){
            if(!answer.hasAnswer()){
                emptyAnswers.add(answer);
            }
            answer.setResult(result);
        }
        answerList.removeAll(emptyAnswers);
        result.setAnswers(answerList);

        // Save answers to DB
        surveyResultDAO.create(result);

        return "/survey/success?faces-redirect=true";
    }

    @Transactional
    public String deleteSurvey(Long id){
        List<Question> questions = questionDAO.findBySurveyId(id);

        survey = surveyDAO.findById(id);
        try {
            for (Question q: questions){
                Long questionId = q.getId();
                List<Answer> answers = answerDAO.findByQuestionId(questionId);
                for (Answer a: answers){
                    answerDAO.delete(a);
                }

                List<Choice> choices = choiceDAO.findByQuestionId(questionId);
                for (Choice c: choices){
                    choiceDAO.delete(c);
                }

                questionDAO.delete(q);
            }
            surveyDAO.delete(survey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
