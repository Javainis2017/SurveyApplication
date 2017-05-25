package com.javainis.survey.controllers.create;

import com.javainis.survey.dao.ConditionDAO;
import com.javainis.survey.dao.QuestionDAO;
import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.*;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named
@ViewScoped
public class NewSurveyController implements Serializable {

    @Inject
    private SurveyDAO surveyDAO;
    @Inject
    private QuestionDAO questionDAO;
    @Inject
    private ConditionDAO conditionDAO;
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
    @Getter
    private Condition condition;
    @Getter
    @Setter
    private Choice conditionalChoice = null;
    @Getter
    @Setter
    private Question conditionalQuestion = null;

    private List<Condition> removableConditions = new ArrayList<>();

    private Timestamp convertToExpirationTimestamp(String date, String time) {
        String fullDate;

        if (!time.isEmpty()) {
            fullDate = date + " " + time + ":00";
        } else {
            fullDate = date + " 23:59:59";
        }

        return Timestamp.valueOf(fullDate);
    }

    @PostConstruct
    public void init() throws Exception {
        if (surveyUrl != null) {
            Survey surveyToEdit = surveyDAO.findByUrl(surveyUrl);
            /* Check if current user is author and survey has no results */
            if ((surveyToEdit != null) && (surveyToEdit.getAuthor().equals(userController.getUser())) && (surveyToEdit.getSurveyResults().isEmpty())) {
                survey = surveyToEdit;
                editingSurvey = true;
                if (survey.getExpirationTime() != null)
                    setDateAndTimeFromDB(survey);
            } else {
                /* Error */
                throw new Exception();
            }
        } else {
            /* Add first page */
            SurveyPage page = new SurveyPage();
            page.setNumber(1);
            page.setSurvey(survey);
            survey.getPages().add(page);
        }
    }

    public void createQuestion(String type, SurveyPage page) {
        surveyCreationStep = SURVEY_CREATION_STEP.NEW_QUESTION;
        newQuestionType = type;
        currentPage = page;
    }

    private int findLastQuestionPosition(SurveyPage page) {
        int lastPosition = 0;
        for (Question question : survey.getQuestions()) {
            if (question.getPage().getNumber() <= page.getNumber()) {
                if (lastPosition < question.getPosition()) {
                    lastPosition = question.getPosition();
                }
            }
        }
        return lastPosition;
    }

    public void saveQuestion(Question question) {
        /* Check if question is new */
        if (surveyCreationStep == SURVEY_CREATION_STEP.NEW_QUESTION) {
            question.setSurvey(survey);

            /* Set page */
            question.setPage(currentPage);
            currentPage.getQuestions().add(question);

            /* Set position */
            int lastPosition = findLastQuestionPosition(currentPage);
            question.setPosition(lastPosition + 1);

            survey.getQuestions().add(lastPosition, question);
            for (Question otherQuestion : survey.getQuestions()) {
                if (otherQuestion.getPage().getNumber() > currentPage.getNumber()) {
                    otherQuestion.setPosition(otherQuestion.getPosition() + 1);
                }
            }
        }
        else if (surveyCreationStep == SURVEY_CREATION_STEP.EDIT_QUESTION){
            List<Condition> removableConditions = new ArrayList<>();
            List<Condition> conditions = new ArrayList<>();
            List<Choice> choices = new ArrayList<>();
            if(question.getClass().getSimpleName().equals("SingleChoiceQuestion")) {
                SingleChoiceQuestion choiceQuestion = (SingleChoiceQuestion)question;
                conditions = choiceQuestion.getDependentConditions();
                choices = choiceQuestion.getChoices();
            }
            else if(question.getClass().getSimpleName().equals("MultipleChoiceQuestion")) {
                MultipleChoiceQuestion choiceQuestion = (MultipleChoiceQuestion)question;
                conditions = choiceQuestion.getDependentConditions();
                choices = choiceQuestion.getChoices();
            }
            for(Condition condition: conditions){
                if(!choices.contains(condition.getChoice())) {
                    removableConditions.add(condition);
                }
            }
            for (Condition condition : removableConditions) {
                removeCondition(condition);
            }
        }
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        questionToEdit = null;
        currentPage = null;
    }

    public void editQuestion(Question question) {
        surveyCreationStep = SURVEY_CREATION_STEP.EDIT_QUESTION;
        questionToEdit = question;
    }

    public void cancel() {
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        questionToEdit = null;
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("singleChoiceQuestionController");
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("multipleChoiceQuestionController");
    }

    public void removeQuestion(Question question) {
        List<Condition> removableConditions = new ArrayList<>();
        for (Condition condition : question.getConditions()) {
            removableConditions.add(condition);
        }
        for (Condition condition : question.getDependentConditions()){
            removableConditions.add(condition);
        }
        for (Condition condition : removableConditions) {
            removeCondition(condition);
        }
        survey.getQuestions().remove(question);
        question.setSurvey(null);

        question.getPage().getQuestions().remove(question);
        /* Renumber remaining questions*/
        for (Question otherQuestion : survey.getQuestions()) {
            if (otherQuestion.getPosition() > question.getPosition()) {
                otherQuestion.setPosition(otherQuestion.getPosition() - 1);
            }
        }
        if (questionToEdit == question) {
            surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        }
    }

    public void createPage() {
        SurveyPage page = new SurveyPage();
        page.setNumber(survey.getPages().size() + 1);
        page.setSurvey(survey);
        survey.getPages().add(page);
    }

    public void removePage(SurveyPage page) {
        if (survey.getPages().size() > 1) {
            List<Question> questionsToRemove = new ArrayList<>(page.getQuestions());
            for (Question question : questionsToRemove) {
                removeQuestion(question);
            }
            survey.getPages().remove(page);
            /* Renumber pages */
            for (SurveyPage surveyPage : survey.getPages()) {
                if (surveyPage.getNumber() > page.getNumber()) {
                    surveyPage.setNumber(surveyPage.getNumber() - 1);
                }
            }
        } else {
            Messages.addGlobalInfo("Cannot remove last page");
        }
    }

    public void moveQuestion(Question question, String direction) {
        Question otherQuestion = null;
        if (direction.equals("up") && question.getPosition() >= 1) {
            /* if first question is on page other than first */
            if (question.getPosition() == 1 && question.getPage().getNumber() > 1) {
                question.getPage().getQuestions().remove(question);
                question.setPage(survey.getPages().get(question.getPage().getNumber() - 2));
                question.getPage().getQuestions().add(question);
                return;
            }
            otherQuestion = survey.getQuestions().get(question.getPosition() - 2);
            /* Move to other page*/
            if (!question.getPage().equals(otherQuestion.getPage())) {
                question.getPage().getQuestions().remove(question);
                question.setPage(otherQuestion.getPage());
                question.getPage().getQuestions().add(question);
            } else { /* Move up in same page*/
                int position = question.getPosition();
                question.setPosition(position - 1);
                otherQuestion.setPosition(position);
                Collections.swap(survey.getQuestions(), position - 2, position - 1); //masyvai nuo nulio, position nuo 1
                List<Question> pageQuestionList = question.getPage().getQuestions();
                Collections.swap(pageQuestionList, pageQuestionList.indexOf(question), pageQuestionList.indexOf(otherQuestion));
            }
        } else if (direction.equals("down") && question.getPosition() <= survey.getQuestions().size()) {
            if (question.getPosition() == survey.getQuestions().size() && question.getPage().getNumber() < survey.getPages().size()) {
                question.getPage().getQuestions().remove(question);
                question.setPage(survey.getPages().get(question.getPage().getNumber()));
                question.getPage().getQuestions().add(question);
                return;
            }
            otherQuestion = survey.getQuestions().get(question.getPosition());
            /* Move to other page*/
            if (!question.getPage().equals(otherQuestion.getPage())) {
                question.getPage().getQuestions().remove(question);
                question.setPage(otherQuestion.getPage());
                question.getPage().getQuestions().add(0, question);
            } else { /* Move down in same page*/
                int position = question.getPosition();
                question.setPosition(position + 1);
                otherQuestion.setPosition(position);
                Collections.swap(survey.getQuestions(), position - 1, position);
                List<Question> pageQuestionList = question.getPage().getQuestions();
                Collections.swap(pageQuestionList, pageQuestionList.indexOf(question), pageQuestionList.indexOf(otherQuestion));
            }
        }
        List<Condition> removableConditions = new ArrayList<>();
        if (otherQuestion != null) {
            for (Condition condition : otherQuestion.getConditions()) {
                if (condition.getQuestion() == question) {
                    removableConditions.add(condition);
                }
            }
            for (Condition condition : question.getConditions()) {
                if (condition.getQuestion() == otherQuestion) {
                    removableConditions.add(condition);
                }
            }
            for (Condition condition : removableConditions) {
                removeCondition(condition);
            }
        }
    }

    public String cancelEdit() {
        return "/home?faces-redirect=true";
    }

    @Transactional
    public String overwrite() {
        survey.setOptLockVersion(conflictingSurvey.getOptLockVersion());
        saveSurvey();
        return "/home?faces-redirect=true";
    }

    public void refresh() {
        survey = surveyDAO.findByUrl(survey.getUrl());
    }

    @Transactional
    @Logged
    public String saveSurvey() {
        /* Check if survey has questions */
        if (survey.getQuestions().isEmpty()) {
            Messages.addGlobalInfo("Survey must have at least 1 question.");
            return null;
        }
        /* Check for empty pages */
        for (SurveyPage page : survey.getPages()) {
            if (page.getQuestions().isEmpty()) {
                Messages.addGlobalInfo("Every page must have at least 1 question.");
                return null;
            }
        }

        if (!expirationDateString.isEmpty()) {
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

        if (!expirationTimeString.isEmpty() && expirationDateString.isEmpty()) {
            Messages.addGlobalInfo("Can not set time without date.");
            return null;
        }

        if (expirationTimeString.isEmpty() && expirationDateString.isEmpty()) {
            survey.setExpirationTime(null);
        }

        if (!editingSurvey) {
            /* Generate unique URL */
            String url = randomStringGenerator.generateString(32);
            /* Check if URL is duplicate */
            while (surveyDAO.existsByUrl(url)) {
                url = randomStringGenerator.generateString(32);
            }
            survey.setUrl(url);

            User currentUser = userController.getUser();
            survey.setAuthor(currentUser);

            /* Persist survey */
            surveyDAO.create(survey);
            entityManager.flush();
            for (Condition condition : survey.getConditions()) {
                conditionDAO.create(condition);
            }
        } else {
            /* Save edited survey */
            try {
                for (Condition condition : removableConditions) {
                    conditionDAO.delete(condition);
                }
                removableConditions = new ArrayList<>();
                surveyDAO.update(survey);
            } catch (OptimisticLockException ole) {
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

    public void addCondition(Question question) {
        surveyCreationStep = SURVEY_CREATION_STEP.NEW_CONDITION;
        condition = new Condition();
        questionToEdit = question;
    }

    public void editCondition(Condition condition) {
        surveyCreationStep = SURVEY_CREATION_STEP.EDIT_CONDITION;
        conditionalQuestion = condition.getQuestion();
        conditionalChoice = condition.getChoice();
        questionToEdit = condition.getDependentQuestion();
        this.condition = condition;
    }

    /**
     * Gauna validžių klausimų, tinkančių sąlygoms kurti, sąrašą
     */
    public List<Question> getChoiceQuestions() {
        List<Question> questions = survey.getQuestions();
        List<Question> choiceQuestions = new ArrayList<>();
        //Atrenkam klausimus, kurie yra single/multi, ir esantys anksčiau keičiamo sąlyginio klausimo
        if (surveyCreationStep == SURVEY_CREATION_STEP.EDIT_CONDITION || surveyCreationStep == SURVEY_CREATION_STEP.NEW_CONDITION) {
            for (Question question : questions) {
                if ((question.getClass().getSimpleName().equals("SingleChoiceQuestion") ||
                        question.getClass().getSimpleName().equals("MultipleChoiceQuestion")) && (question.getPosition() < questionToEdit.getPosition())) {
                    choiceQuestions.add(question);
                }
            }
        }
        //Atrūšiuojam klausimus, kuriems jau yra parinkti visi choice'ai
        if (surveyCreationStep == SURVEY_CREATION_STEP.EDIT_CONDITION) {
            for (Condition condition : questionToEdit.getConditions()) {
                if (condition.getChoice() == null && this.condition != condition && choiceQuestions.contains(condition.getQuestion())) {
                    choiceQuestions.remove(condition.getQuestion());
                }
            }
        } else if (surveyCreationStep == SURVEY_CREATION_STEP.NEW_CONDITION) {
            for (Condition condition : questionToEdit.getConditions()) {
                if (condition.getChoice() == null && choiceQuestions.contains(condition.getQuestion())) {
                    choiceQuestions.remove(condition.getQuestion());
                }
            }
        }
        return choiceQuestions;
    }

    public void saveCondition() {
        if (surveyCreationStep == SURVEY_CREATION_STEP.NEW_CONDITION) {
            //Nustatomos reikšmės
            condition.setChoice(conditionalChoice);
            condition.setDependentQuestion(questionToEdit);
            condition.setQuestion(conditionalQuestion);
            condition.setSurvey(survey);
            //Sąlyga pridedama į atitinkamus klausimus ir survey
            questionToEdit.getConditions().add(condition);
            conditionalQuestion.getDependentConditions().add(condition);
            survey.getConditions().add(condition);
        }
        if (surveyCreationStep == SURVEY_CREATION_STEP.EDIT_CONDITION) {
            //Pašalinamas senas condition iš survey ir klausimų
            condition.getQuestion().getDependentConditions().remove(condition);
            //Nustatomos naujos reikšmės
            condition.setChoice(conditionalChoice);
            condition.setQuestion(conditionalQuestion);
            //Sąlyga pridedama į atitinkamus klausimus ir survey
            conditionalQuestion.getDependentConditions().add(condition);
        }
        //Šalina silpnesnius condition'us
        List<Condition> removableConditions = new ArrayList<>();
        if (conditionalChoice == null) {
            for (Condition questionCondition : questionToEdit.getConditions()) {
                if (questionCondition.getQuestion() == conditionalQuestion && !questionCondition.equals(condition)) {
                    removableConditions.add(questionCondition);
                }
            }
            for (Condition condition : removableConditions) {
                removeCondition(condition);
            }
        }
        closeConditionDialog();
    }

    public void closeConditionDialog() {
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
        condition = null;
        conditionalChoice = null;
        conditionalQuestion = null;
        questionToEdit = null;
    }

    public void removeCondition(Condition condition) {
        if (this.condition == condition) {
            this.condition = null;
        }
        if (condition.getId() != null) {
            removableConditions.add(condition);
        }
        condition.getDependentQuestion().getConditions().remove(condition);
        condition.getQuestion().getDependentConditions().remove(condition);
        survey.getConditions().remove(condition);
        condition.setDependentQuestion(null);
        condition.setSurvey(null);
        condition.setQuestion(null);
        condition.setChoice(null);
        surveyCreationStep = SURVEY_CREATION_STEP.QUESTION_TYPE_CHOICE;
    }

    public List<Choice> findQuestionChoices() {
        if (questionToEdit != null && conditionalQuestion != null) {
            List<Condition> conditions = questionToEdit.getConditions();
            List<Choice> result = new ArrayList<>();
            if (conditionalQuestion.getClass().getSimpleName().equals("SingleChoiceQuestion")) {
                result.addAll(((SingleChoiceQuestion) conditionalQuestion).getChoices());
            } else if (conditionalQuestion.getClass().getSimpleName().equals("MultipleChoiceQuestion")) {
                result.addAll(((MultipleChoiceQuestion) conditionalQuestion).getChoices());
            }
            //Iteruojame per visas klausimo sąlygas
            for (Condition condition : conditions) {
                if (condition.getQuestion() == conditionalQuestion) {
                    if (surveyCreationStep == SURVEY_CREATION_STEP.NEW_CONDITION) {
                        //Iteruoja per choice'us ir šalina iš galutinio sąrašo,
                        //Jei randa tuščią, sustabdo(jau parinkti visi variantai)
                        Choice choice = condition.getChoice();
                        if (choice == null) {
                            result = null;
                            break;
                        } else if (result.contains(choice)) {
                            result.remove(choice);
                        }
                    }
                    if (surveyCreationStep == SURVEY_CREATION_STEP.EDIT_CONDITION) {
                        if (!condition.equals(this.condition)) {
                            Choice choice = condition.getChoice();
                            if (choice == null) {
                                result = null;
                                break;
                            } else if (result.contains(choice)) {
                                result.remove(choice);
                            }
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    /**
     * Checks whether question moving affects any conditions
     */
    public Boolean checkConditionsMoving(Question question, int positionChange) {
        //Finds swapped question
        Question swappedQuestion = findQuestionAtPosition(question.getPosition() + positionChange);
        if (swappedQuestion != null) {
            for (Condition condition : swappedQuestion.getConditions()) {
                if (condition.getQuestion() == question) {
                    return false;
                }
            }
            for (Condition condition : question.getConditions()) {
                if (condition.getQuestion() == swappedQuestion) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds question with given position
     */
    private Question findQuestionAtPosition(int i) {
        //Checks if position does not exist(when creating web page)
        if (i <= 0 || i > survey.getQuestions().size()) {
            return null;
        }
        return survey.getQuestions().get(i - 1);
    }

    /**
     * Warning for "Any choice" selection
     */
    public void warnAnyChoice() {
        if (conditionalQuestion != null && conditionalChoice == null) {
            FacesContext.getCurrentInstance().addMessage("warnAnyChoice", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "Selecting 'Any choice' will delete weaker conditions"));
        }
    }

    public enum SURVEY_CREATION_STEP {
        QUESTION_TYPE_CHOICE, NEW_QUESTION, EDIT_QUESTION, NEW_CONDITION, EDIT_CONDITION
    }
}
