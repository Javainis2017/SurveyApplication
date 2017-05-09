package com.javainis.survey.controllers.create;

import com.javainis.survey.dao.QuestionDAO;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Question;
import com.javainis.survey.entities.Survey;
import com.javainis.user_management.controllers.UserController;
import com.javainis.user_management.entities.User;
import com.javainis.utility.ExpirationChecker;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.cdi.Param;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Timestamp;

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
    private boolean editingSurvey = false;

    @Getter
    @Setter
    private String expirationDateString;

    @Getter
    @Setter
    private String expirationTimeString;

    private Timestamp convertToExpirationTimestamp(String date, String time)
    {
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
        }
    }

    public void createQuestion(String type){
        surveyCreationStep = SURVEY_CREATION_STEP.NEW_QUESTION;
        newQuestionType = type;
    }

    public void saveQuestion(Question question){
        //Check if question is new
        if(surveyCreationStep == SURVEY_CREATION_STEP.NEW_QUESTION){
            question.setSurvey(survey);
            survey.getQuestions().add(question);
        }
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        questionToEdit = null;
    }

    public void editQuestion(Question question){
        surveyCreationStep = SURVEY_CREATION_STEP.EDIT_QUESTION;
        questionToEdit = question;
    }

    public void cancel(){
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        questionToEdit = null;
    }

    public void removeQuestion(Question question){
        question.setSurvey(null);
        survey.getQuestions().remove(question);
        if(questionToEdit == question){
            surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
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
    public String saveSurvey(){
        /* Check if survey has questions */
        if(survey.getQuestions().isEmpty()){
            Messages.addGlobalInfo("Survey must have at least 1 question.");
            return null;
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
            /* Generate unique URL*/
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
