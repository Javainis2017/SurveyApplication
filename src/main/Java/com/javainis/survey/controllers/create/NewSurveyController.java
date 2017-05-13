package com.javainis.survey.controllers.create;

import com.javainis.survey.dao.QuestionDAO;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Question;
import com.javainis.survey.entities.Survey;
import com.javainis.survey.entities.SurveyPage;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.entities.User;
import com.javainis.utility.ExpirationChecker;
import com.javainis.utility.Logs.Logged;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.cdi.Param;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;


@Named
@ViewScoped
public class NewSurveyController implements Serializable{

    public enum SURVEY_CREATION_STEP {
        QUESTION_TYPE_CHOICE, NEW_QUESTION, EDIT_QUESTION
    }

    @Inject
    private SurveyDAO surveyDAO;

    @Inject
    private QuestionDAO questionDAO;

    @Inject
    @Getter
    private UserController userController;

    @Inject
    private RandomStringGenerator randomStringGenerator;

    @Inject
    @Param(pathIndex = 0)
    private String surveyUrl;

    @Inject
    private ExpirationChecker expirationChecker;

    @Inject
    private EntityManager entityManager;

    @Getter
    private Survey survey = new Survey();

    @Getter
    private Survey conflictingSurvey;

    @Getter
    private SURVEY_CREATION_STEP surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;

    /* Naujai kuriamo klausimo tipas*/
    @Getter
    private String newQuestionType;

    @Getter
    private Question questionToEdit;

    @Getter
    private SurveyPage currentPage;

    @Getter
    private boolean editingSurvey = false;

    @Getter
    @Setter
    private String expirationDateString;

    @Getter
    @Setter
    private String expirationTimeString;

    private Timestamp convertToExpirationTimestamp(String date, String time){
        String fullDate;

        if(!time.isEmpty())
        {
            fullDate = date + " " + time + ":00";
        }
        else
        {
            fullDate = date + " 23:59:59";
        }

        return Timestamp.valueOf(fullDate);
    }

    @PostConstruct
    public void init() throws Exception{
        if(surveyUrl != null){
            Survey surveyToEdit = surveyDAO.findByUrl(surveyUrl);
            /* Check if current user is author and survey has no results */
            if((surveyToEdit != null) && (surveyToEdit.getAuthor().equals(userController.getUser())) && (surveyToEdit.getSurveyResults().isEmpty())){
                survey = surveyToEdit;
                editingSurvey = true;
                if(survey.getExpirationTime() != null)
                    setDateAndTimeFromDB(survey);
            }else{
                /* Error */
                throw new Exception();
            }
        }else{
            /* Add first page */
            SurveyPage page = new SurveyPage();
            page.setNumber(1);
            page.setSurvey(survey);
            survey.getPages().add(page);
        }
    }

    public void createQuestion(String type, SurveyPage page){
        surveyCreationStep = SURVEY_CREATION_STEP.NEW_QUESTION;
        newQuestionType = type;
        currentPage = page;
    }

    private int findLastQuestionPosition(SurveyPage page){
        int lastPosition = 0;
        for(Question question : survey.getQuestions()){
            if(question.getPage().getNumber() <= page.getNumber()){
                if(lastPosition < question.getPosition()){
                    lastPosition = question.getPosition();
                }
            }
        }
        return lastPosition;
    }
    public void saveQuestion(Question question){
        /* Check if question is new */
        if(surveyCreationStep == SURVEY_CREATION_STEP.NEW_QUESTION){
            question.setSurvey(survey);

            /* Set page */
            question.setPage(currentPage);
            currentPage.getQuestions().add(question);

            /* Set position */
            int lastPosition = findLastQuestionPosition(currentPage);
            question.setPosition(lastPosition + 1);

            survey.getQuestions().add(lastPosition, question);
            for(Question otherQuestion: survey.getQuestions()){
                if(otherQuestion.getPage().getNumber() > currentPage.getNumber()){
                    otherQuestion.setPosition(otherQuestion.getPosition() + 1);
                }
            }
        }
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        questionToEdit = null;
        currentPage = null;
    }

    public void editQuestion(Question question){
        surveyCreationStep = SURVEY_CREATION_STEP.EDIT_QUESTION;
        questionToEdit = question;
    }

    public void cancel(){
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        questionToEdit = null;
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("singleChoiceQuestionController");
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("multipleChoiceQuestionController");
    }

    public void removeQuestion(Question question){
        survey.getQuestions().remove(question);
        question.setSurvey(null);

        question.getPage().getQuestions().remove(question);
        question.setPage(null);
        /* Renumber remaining questions*/
        for(Question otherQuestion: survey.getQuestions()){
            if(otherQuestion.getPosition() > question.getPosition()){
                otherQuestion.setPosition(otherQuestion.getPosition() - 1);
            }
        }
        if(questionToEdit == question){
            surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        }
    }

    public void createPage(){
        SurveyPage page = new SurveyPage();
        page.setNumber(survey.getPages().size() + 1);
        page.setSurvey(survey);
        survey.getPages().add(page);
    }

    public void removePage(SurveyPage page){
        if(survey.getPages().size() > 1){
            int lastPosition = 0;
            if(!page.getQuestions().isEmpty()){
                lastPosition = page.getQuestions().get(0).getPosition();
            }
            for(Question question: page.getQuestions()){
                survey.getQuestions().remove(question);
                question.setSurvey(null);
            }
            survey.getPages().remove(page);
            /* Renumber pages */
            for(SurveyPage surveyPage: survey.getPages()){
                if(surveyPage.getNumber() > page.getNumber()){
                    surveyPage.setNumber(surveyPage.getNumber() - 1);
                    /* Renumber questions */
                    for(Question question: surveyPage.getQuestions()){
                        if(question.getPosition() > lastPosition){
                            question.setPosition(question.getPosition() - page.getQuestions().size());
                        }
                    }
                }
            }
        }else{
            Messages.addGlobalInfo("Cannot remove last page");
        }
    }

    public void moveQuestion(Question question, String direction){
        Question otherQuestion;
        if(direction.equals("up") && question.getPosition() >= 1){
            /* if first question is on page other than first */
            if(question.getPosition() == 1 && question.getPage().getNumber() > 1 ){
                question.getPage().getQuestions().remove(question);
                question.setPage(survey.getPages().get(question.getPage().getNumber() - 2));
                question.getPage().getQuestions().add(question);
                return;
            }
            otherQuestion = survey.getQuestions().get(question.getPosition() - 2);
            /* Move to other page*/
            if(!question.getPage().equals(otherQuestion.getPage())){
                question.getPage().getQuestions().remove(question);
                question.setPage(otherQuestion.getPage());
                question.getPage().getQuestions().add(question);
            }else{ /* Move up in same page*/
                int position = question.getPosition();
                question.setPosition(position - 1);
                otherQuestion.setPosition(position);
                Collections.swap(survey.getQuestions(), position - 2, position - 1); //masyvai nuo nulio, position nuo 1
                List<Question> pageQuestionList = question.getPage().getQuestions();
                Collections.swap(pageQuestionList, pageQuestionList.indexOf(question), pageQuestionList.indexOf(otherQuestion));
            }
        }else if(direction.equals("down") && question.getPosition() <= survey.getQuestions().size()){
            if(question.getPosition() == survey.getQuestions().size() && question.getPage().getNumber() < survey.getPages().size() ){
                question.getPage().getQuestions().remove(question);
                question.setPage(survey.getPages().get(question.getPage().getNumber()));
                question.getPage().getQuestions().add(question);
                return;
            }
            otherQuestion = survey.getQuestions().get(question.getPosition());
            /* Move to other page*/
            if(!question.getPage().equals(otherQuestion.getPage())){
                question.getPage().getQuestions().remove(question);
                question.setPage(otherQuestion.getPage());
                question.getPage().getQuestions().add(question);
            }else{ /* Move down in same page*/
                int position = question.getPosition();
                question.setPosition(position + 1);
                otherQuestion.setPosition(position);
                Collections.swap(survey.getQuestions(), position - 1, position);
                List<Question> pageQuestionList = question.getPage().getQuestions();
                Collections.swap(pageQuestionList, pageQuestionList.indexOf(question), pageQuestionList.indexOf(otherQuestion));
            }
        }
    }

    public String cancelEdit(){
        return "/home?faces-redirect=true";
    }

    @Transactional
    public String overwrite(){
        survey.setOptLockVersion(conflictingSurvey.getOptLockVersion());
        saveSurvey();
        return "/home?faces-redirect=true";
    }

    public void refresh(){
        survey = surveyDAO.findByUrl(survey.getUrl());
    }

    @Transactional
    @Logged
    public String saveSurvey(){
        /* Check if survey has questions */
        if(survey.getQuestions().isEmpty()){
            Messages.addGlobalInfo("Survey must have at least 1 question.");
            return null;
        }
        /* Check for empty pages */
        for(SurveyPage page: survey.getPages()){
            if(page.getQuestions().isEmpty()){
                Messages.addGlobalInfo("Every page must have at least 1 question.");
                return null;
            }
        }

        if(!expirationDateString.isEmpty()) {
            Timestamp timestamp;
            try {
                timestamp = convertToExpirationTimestamp(expirationDateString, expirationTimeString);
            } catch (Exception e) {
                Messages.addGlobalInfo("Wrong expiration time.");
                return null;
            }

            if (expirationChecker.isExpired(timestamp)) {
                Messages.addGlobalInfo("Wrong expiration time.");
                return null;
            }

            survey.setExpirationTime(timestamp);
        }

        if(!expirationTimeString.isEmpty() && expirationDateString.isEmpty()){
            Messages.addGlobalInfo("Can not set time without date.");
            return null;
        }

        if(expirationTimeString.isEmpty() && expirationDateString.isEmpty()){
           survey.setExpirationTime(null);
        }

        if(!editingSurvey){
            /* Generate unique URL */
            String url = randomStringGenerator.generateString(32);
            /* Check if URL is duplicate */
            while(surveyDAO.existsByUrl(url)){
                url = randomStringGenerator.generateString(32);
            }
            survey.setUrl(url);

            User currentUser = userController.getUser();
            survey.setAuthor(currentUser);

            /* Persist survey */
            surveyDAO.create(survey);
        }else{
            /* Save edited survey */
            try {
                surveyDAO.update(survey);
            }catch (OptimisticLockException ole){
                conflictingSurvey = surveyDAO.findById(survey.getId());
                conflictingSurvey.getQuestions().size();
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                RequestContext.getCurrentInstance().execute("PF('oleDialog').show();");
                return null;
            }
        }
        return "/home?faces-redirect=true";
    }

    private void setDateAndTimeFromDB(Survey survey) {
        String[] splitList = survey.getExpirationTime().toString().split(" ");
        expirationDateString = splitList[0];
        expirationTimeString = splitList[1].substring(0, splitList[1].length() - 5);
    }
}
