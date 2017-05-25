package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.User;
import com.javainis.utility.HashGenerator;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;

@Named
@SessionScoped
public class UserController implements Serializable
{
    @Getter
    private User user = new User();

    @Inject
    @Getter
    private UserDAO userDAO;

    @Inject
    private HashGenerator hashGenerator;

    @Getter
    private String passwordHash;

    @Transactional
    public String login() {
        try{
            user = userDAO.login(user.getEmail(), passwordHash);
            resetPasswordFields();
            if (user.getBlocked()) {
                Messages.addGlobalWarn("You are blocked from system");
                resetPasswordFields();
                user = new User();
                return null;
            }
            return "home?faces-redirect=true";
        }
        catch (NoResultException ex) {
            //Nerado tokio vartotojo
            Messages.addGlobalWarn("Incorrect email or password");
            resetPasswordFields();
            user = new User();
            return null;
        }
    }

    @Transactional
    public String logout(){
        try{
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect("/");
            context.invalidateSession();
        }
        catch (Exception ex){
            Messages.addGlobalWarn("Could not log you out. Now you're stuck forever :(");
        }
        return null;
    }

    public void checkAlreadyLoggedIn() throws IOException{
        if (user.getUserID() != null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/home");
        }
    }

    public void refreshUser(){
        user = userDAO.findById(user.getUserID());
    }

    public void setPasswordHash(String password) {
        if(!password.isEmpty()){
            passwordHash = hashGenerator.generatePasswordHash(password);
        }
    }

    private void resetPasswordFields() {
        passwordHash = "";
    }
}
