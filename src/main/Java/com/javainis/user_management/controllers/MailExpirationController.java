package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.MailExpirationDAO;
import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.MailExpiration;
import com.javainis.user_management.entities.User;
import com.javainis.utility.HashGenerator;
import com.javainis.utility.RandomStringGenerator;
import com.javainis.utility.mail.MailSender;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.cdi.Param;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;

@Named
@RequestScoped
public class MailExpirationController {
    @Inject
    private MailExpirationDAO mailExpirationDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private HashGenerator hashGenerator;

    @Inject
    private RandomStringGenerator randomStringGenerator;

    @Inject
    private MailSender mailSender;

    @Inject
    @Param(pathIndex = 0)
    @Getter
    private String url;

    @Getter
    private User user;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String newPassword;

    @Getter
    @Setter
    private String repeatedPassword;

    @Getter
    private MailExpiration mailExpiration = new MailExpiration();

    @Getter
    private Boolean success = false;

    @Transactional
    public void doPost() throws ServletException, IOException, NamingException {
        String messagePart = "Your password reminder link";
        String subject = "Password reminder";
        String path = "user-management/change-password-email/";

        boolean isRegistered = userDAO.emailIsRegistered(email);
        if(isRegistered){
            Context ctx = new InitialContext();
            Context env = (Context) ctx.lookup("java:comp/env");
            final String host = (String) env.lookup("Host");

            setSentEmailProperties();
            String message = messagePart + ": " + host + path + mailExpiration.getUrl();


            mailSender.sendEmail(email, subject, message);

            findAndRemoveOlderMails(email);

            mailExpirationDAO.create(mailExpiration);
            url = mailExpiration.getUrl();
        }
        Messages.addGlobalInfo("Email was sent successfully");
        email = "";
    }

    private void findAndRemoveOlderMails(String email) {
        User user = userDAO.getUserByEmail(email);
        mailExpirationDAO.removeFromMailExpiration(user);
    }

    private void setSentEmailProperties()
    {
        mailExpiration.setUser(userDAO.getUserByEmail(email));

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        mailExpiration.setExpirationDate(timestamp);
        long duration = 48 * 60 * 60 * 1000;
        mailExpiration.getExpirationDate().setTime(timestamp.getTime() + duration);

        String genUrl = randomStringGenerator.generateString(32);
        while (mailExpirationDAO.existsByUrl(genUrl)) {
            genUrl = randomStringGenerator.generateString(32);
        }
        mailExpiration.setUrl(genUrl);
    }

    @Transactional
    public void changePassword(){
        try{
            user = mailExpirationDAO.findMailExpiration(url).getUser();
            if(user == null){
                throw new Exception("Stop hacking");
            }

            if(!newPassword.contentEquals(repeatedPassword)) {
                Messages.addGlobalWarn("New and repeated password are not equal");
                return;
            }

            newPassword = hashGenerator.generatePasswordHash(newPassword);
            userDAO.changeUserPassword(user.getEmail(), newPassword);
            user.setPasswordHash(newPassword);
            mailExpirationDAO.removeFromMailExpiration(user);
            Messages.addGlobalInfo("Password was successfully changed");
            resetPasswordFields();
            success = true;
        }
        catch(Exception ex){
            Messages.addGlobalWarn("User password change failed");
        }
    }

    private void resetPasswordFields(){
        newPassword = "";
        repeatedPassword = "";
    }

    @PostConstruct
    private void init() {
        if(url == null){
            return;
        }

        try{
            mailExpiration = mailExpirationDAO.findMailExpiration(url);
        }catch (NoResultException ex){
            return;
        }

        if(mailExpiration == null){
            url = null;
        }else{
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if(timestamp.before(mailExpiration.getExpirationDate()))
                user = mailExpiration.getUser();
        }
    }
}
