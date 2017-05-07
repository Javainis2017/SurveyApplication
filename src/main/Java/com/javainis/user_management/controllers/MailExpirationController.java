package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.MailExpirationDAO;
import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.MailExpiration;
import com.javainis.user_management.entities.User;
import com.javainis.utility.HashGenerator;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.cdi.Param;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;

@Named
@RequestScoped
public class MailExpirationController {
    @Getter
    private MailExpiration mailExpiration = new MailExpiration();

    @Getter
    @Setter
    private String email;

    @Inject
    private MailExpirationDAO mailExpirationDAO;

    @Inject
    @Getter
    private UserDAO userDAO;

    @Getter
    private User user;

    @Inject
    private HashGenerator hashGenerator;

    @Inject
    private RandomStringGenerator randomStringGenerator;

    @Inject
    @Param(pathIndex = 0)
    @Getter
    private String url;

    @Getter
    @Setter
    private String newPassword;

    @Getter
    @Setter
    private String repeatedPassword;

    @Transactional
    public void doPost() throws ServletException, IOException, NamingException {
        if(userDAO.emailIsRegistered(email))
        {
            Context ctx = new InitialContext();
            Context env = (Context) ctx.lookup("java:comp/env");
            String subject = "Password reminder";
            final String fromEmail = (String) env.lookup("FromEmail");
            final String username = (String) env.lookup("Username");
            final String password = (String) env.lookup("Password");

            setSentEmailProperties();
            String message = "Your password reminder link: http://localhost:8080/user-management/change-password-email/" + mailExpiration.getUrl();
            if(true == sendEmail(fromEmail, username, password, email, subject, message))
            {
                findAndRemoveOlderMails(email);
                mailExpirationDAO.create(mailExpiration);
                url = mailExpiration.getUrl();
                Messages.addGlobalInfo("Email was sent successfully");
            }
            else
            {
                Messages.addGlobalInfo("Email was not sent successfully, try again");
            }
        }
        else
        {
            Messages.addGlobalInfo("Email was sent successfully");
        }
    }

    private void findAndRemoveOlderMails(String email) {
        User user = userDAO.getUserByEmail(email);
        mailExpirationDAO.removeFromMailExpiration(user);
    }

    private boolean sendEmail(String fromEmail, String username, String password, String toEmail, String subject, String message)
    {
        Properties props = new Properties();
        props.put("mail.smtp.user", fromEmail);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtps.ssl.enable", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        session.setDebug(true);

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setText(message);

            Transport transport = session.getTransport("smtps");
            transport.connect("smtp.gmail.com", Integer.valueOf("465"), username, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setSentEmailProperties()
    {
        long duration = 48 * 60 * 60 * 1000;
        mailExpiration.setUser(userDAO.getUserByEmail(email));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        mailExpiration.setExpirationDate(timestamp);
        mailExpiration.getExpirationDate().setTime(timestamp.getTime() + duration);

        String genUrl = randomStringGenerator.generateString(32);
        while(mailExpirationDAO.existsByUrl(genUrl)){
            genUrl = randomStringGenerator.generateString(32);
        }

        mailExpiration.setUrl(genUrl);
    }

    @Transactional
    public void changePassword(){
        try{
            user = mailExpirationDAO.findMailExpiration(url).getUser();
            if(user == null)
                throw new Exception("Stop hacking");

            if(!newPassword.contentEquals(repeatedPassword)) {
                Messages.addGlobalWarn("New and repeated password are not equal");
            }

            newPassword = hashGenerator.generatePasswordHash(newPassword);
            userDAO.changeUserPassword(user.getEmail(), newPassword);
            user.setPasswordHash(newPassword);
            Messages.addGlobalInfo("Password was successfully changed");
            resetPasswordFields();
        }
        catch(Exception ex){
            Messages.addGlobalWarn("FATAL ERROR: User password change failed");
        }
    }

    private void resetPasswordFields()
    {
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

        if(mailExpiration == null)
        {
            url = null;
        }
        else
        {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if(timestamp.before(mailExpiration.getExpirationDate()))
                user = mailExpiration.getUser();
        }

    }
}
