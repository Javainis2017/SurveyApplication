package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.MailExpirationDAO;
import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.MailExpiration;
import com.javainis.user_management.entities.User;
import com.javainis.utility.DateUtil;
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
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

@Named
@RequestScoped
public class MailExpirationController implements Serializable {
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
    private DateUtil dateUtil;

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
    public void doPost() throws ServletException, IOException
    {
        if(userDAO.emailIsRegistered(email))
        {
            String subject = "Password reminder";
            String fromEmail = "javainis2017@gmail.com";
            String username = "javainis2017@gmail.com";
            String password = "javainiai";

            setSentEmailProperties();
            String message = "Your password reminder link: http://localhost:8080/user-management/change-password-email/" + mailExpiration.getUrl();
            sendEmail(fromEmail, username, password, email, subject, message);
            mailExpirationDAO.create(mailExpiration);
            url = mailExpiration.getUrl();
            Messages.addGlobalInfo("Email was sent successfully");
        }
        else
        {
            Messages.addGlobalInfo("User with specified email does not exist");
        }
    }

    private void sendEmail(String fromEmail, String username, String password, String toEmail, String subject, String message)
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

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void setSentEmailProperties()
    {
        Date date = new Date();
        mailExpiration.setUser(userDAO.getUserByEmail(email));
        mailExpiration.setExpirationDate(DateUtil.addDays(date, 2));
        mailExpiration.setUrl(randomStringGenerator.generateString(32));
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
            user = mailExpiration.getUser();
        }

    }
}
