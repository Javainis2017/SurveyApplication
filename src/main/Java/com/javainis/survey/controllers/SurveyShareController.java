package com.javainis.survey.controllers;

import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.entities.Survey;
import com.javainis.utility.mail.MailSender;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Map;

@Named
@ViewScoped
public class SurveyShareController implements Serializable{

    @Inject
    private MailSender mailSender;

    @Inject
    private SurveyDAO surveyDAO;

    @Getter
    @Setter
    private String input;

    @Getter
    private Survey survey;

    @Getter
    private Boolean success = false;

    private String surveyUrl;

    @PostConstruct
    private void init(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        surveyUrl = params.get("url");
        if(surveyUrl != null){
            survey = surveyDAO.findByUrl(surveyUrl);
        }
    }

    public void send(){
        String[] emails = input.split("\\r?\\n");
        for(String email : emails){
            if(!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
                Messages.addGlobalWarn("Invalid email found.");
                return;
            }
        }
        try {
            Context ctx = new InitialContext();
            Context env = (Context) ctx.lookup("java:comp/env");
            final String host = (String) env.lookup("Host");

            String path = "survey/show/";
            String message = "You have been invited to answer this survey: " + host + path + survey.getUrl();
            mailSender.sendEmail(emails, "Invitation to survey \"" + survey.getTitle() + "\"", message);

            Messages.addGlobalInfo("Emails sent successfully.");
            success = true;
        }catch (NamingException ne){
            Messages.addGlobalWarn("Error sending emails.");
        }
    }
}
