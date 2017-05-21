package com.javainis.survey.controllers.show;

import com.javainis.survey.dao.*;
import com.javainis.survey.entities.*;
import com.javainis.utility.RandomStringGenerator;
import com.javainis.utility.mail.MailSender;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.cdi.Param;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

    @Inject
    @Param(pathIndex = 1)
    private String resultUrl;

    @Inject
    private MailSender mailSender;

    @Inject
    private RandomStringGenerator randomStringGenerator;

    @Getter
    private Survey survey;

    @Getter
    private SurveyResult surveyResult;

    @Getter
    private SurveyPage currentPage;

    @Getter
    private Map<Question, Answer> answers = new HashMap<>();

    @Getter
    @Setter
    private String email;

    @Getter
    private Boolean success = false;

    @PostConstruct
    public void init() {
        // Check if parameter exists
        if (surveyUrl == null) {
            return;
        }

        // Find survey
        try {
            survey = surveyDAO.findByUrl(surveyUrl);
            // init questions
            for(SurveyPage page: survey.getPages()){
                page.getQuestions().size();
            }
        } catch (NoResultException ex) {
            return;
        }

        //Find survey results
        if(resultUrl != null) {
            try {
                surveyResult = surveyResultDAO.findByUrl(resultUrl);
            } catch (NoResultException ex) {
                // if resultUrl does not return any results, survey should be presented with empty answer fields
            }
        }

        if(surveyResult != null) {
            for(Answer answer : surveyResult.getAnswers()) {
                answers.put(answer.getQuestion(), answer);
            }
        }

        // Init answer objects
        for(Question question : survey.getQuestions()){
            if(!answers.containsKey(question)) {
                if (question.getClass().getSimpleName().equals("FreeTextQuestion")) {
                    Answer answer = new TextAnswer();
                    answer.setQuestion(question);
                    ((TextAnswer) answer).setText("");
                    answers.put(question, answer);
                } else if (question.getClass().getSimpleName().equals("IntervalQuestion")) {
                    Answer answer = new NumberAnswer();
                    answer.setQuestion(question);
                    answers.put(question, answer);
                } else if (question.getClass().getSimpleName().equals("SingleChoiceQuestion")) {
                    Answer answer = new SingleChoiceAnswer();
                    answer.setQuestion(question);
                    answers.put(question, answer);
                } else if (question.getClass().getSimpleName().equals("MultipleChoiceQuestion")) {
                    Answer answer = new MultipleChoiceAnswer();
                    answer.setQuestion(question);
                    answers.put(question, answer);
                }
            }
        }
        currentPage = survey.getPages().get(0);
    }

    public void goToPage(int number){
        if(number < 1 || number > survey.getPages().size()){
            return;
        }
        int direction;
        if(number < currentPage.getNumber()){
            direction = -1;
        }else{
            direction = 1;
        }
        boolean pageEmpty = true;
        while(pageEmpty){
            if(number < 1 || number > survey.getPages().size()) {
                return;
            }
            SurveyPage page = survey.getPages().get(number - 1);
            for(Question question : page.getQuestions()){
                if(checkQuestionConditions(question)){
                    currentPage = page;
                    return;
                }
            }
            number += direction;
        }
    }

    public boolean isLastPage(){
        if(currentPage.getNumber() == survey.getPages().size()){
            return true;
        }
        for(int i = currentPage.getNumber(); i < survey.getPages().size(); i++){
            SurveyPage page = survey.getPages().get(i);
            for(Question question : page.getQuestions()){
                if(checkQuestionConditions(question)){
                    return false;
                }
            }
        }
        return true;
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
/**
 * Checks whether question should be shown to user, who is filling up a survey
 */
    public Boolean checkQuestionConditions(Question question){
        List<Condition> conditions = question.getConditions();
        //If there are no conditions, always show
        if(conditions.size() == 0) {
            return true;
        }

        for(Condition condition: conditions){
            Answer answer = answers.get(condition.getQuestion());
            //Checks if it's single choice answer
            if(answer.getClass().getSimpleName().equals("SingleChoiceAnswer")) {
                SingleChoiceAnswer singleChoiceAnswer = (SingleChoiceAnswer)answer;
                //When condition is any answer
                if(singleChoiceAnswer.getChoice() != null){
                    if(condition.getChoice() == null) {
                        return true;
                    }//When condition is specific answer
                    else if(condition.getChoice() != null && singleChoiceAnswer.getChoice().equals(condition.getChoice())){
                        return true;
                    }
                }
            }//Checks if it's multiple choice answer
            else if(answer.getClass().getSimpleName().equals("MultipleChoiceAnswer")){
                MultipleChoiceAnswer multipleChoiceAnswer = (MultipleChoiceAnswer)answer;
                //Iterating through selected answers
                for(Choice choice:multipleChoiceAnswer.getChoices()) {
                    //When condition is any answer
                    if(condition.getChoice() == null && choice != null) {
                        return true;
                    }//When condition is specific answer
                    else if (condition.getChoice() != null && choice.equals(condition.getChoice())){
                        return true;
                    }
                }
            }
        }
        //If none of the conditions are met, question will not be shown
        return false;
    }

    /* Save incomplete survey to email */
    @Transactional
    public void saveSurveyToEmail(){
        if(!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
            Messages.addGlobalWarn("Invalid email.");
            return;
        }

        try {
            Context ctx = new InitialContext();
            Context env = (Context) ctx.lookup("java:comp/env");
            final String host = (String) env.lookup("Host");

            String path = "survey/show/";

            String genUrl = randomStringGenerator.generateString(32);
            while (surveyResultDAO.existsByUrl(genUrl)) {
                genUrl = randomStringGenerator.generateString(32);
            }

            String url = survey.getUrl() + "/" + genUrl;
            String message = "Follow this link to fully answer survey: " + host + path + url;
            mailSender.sendEmail(email, "Partly finished survey \"" + survey.getTitle() + "\"", message);

            Messages.addGlobalInfo("Emails sent successfully.");
            success = true;

            SurveyResult result;
            if(surveyResult == null) {
                // Create SurveyResult object
                result = new SurveyResult();
                result.setSurvey(survey);
            }else{
                // Take existing survey results
                result = surveyResult;
            }

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

            result.setComplete(false);

            result.setUrl(genUrl);

            // Save answers to DB
            if(surveyResultDAO.existsById(result.getId())) {
                surveyResultDAO.update(result);
            }else{
                surveyResultDAO.create(result);
            }

            Messages.addGlobalInfo("Partly finished survey was sent to specified email.");

        }catch (NamingException ne){
            Messages.addGlobalWarn("Error sending emails.");
        }
    }
}
