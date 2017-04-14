package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.MailExpirationDAO;
import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.MailExpiration;
import com.javainis.utility.DateUtil;
import com.javainis.utility.RandomStringGenerator;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
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
    @Getter
    private MailExpirationDAO mailExpirationDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private RandomStringGenerator randomStringGenerator;

    @Inject
    private DateUtil dateUtil;

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
            String message = "Your password reminder link: " + " " + "domain.com/password/" + mailExpiration.getUrl();
            sendEmail(fromEmail, username, password, email, subject, message);
            mailExpirationDAO.create(mailExpiration);
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
        mailExpiration.setExpirationDate(dateUtil.addDays(date, 2));
        mailExpiration.setUrl(randomStringGenerator.generateString(32));
    }
}
