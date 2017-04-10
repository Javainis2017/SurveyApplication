package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.User;
import com.javainis.utility.HashGenerator;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
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
    @Setter
    private String passwordHash;

    @Getter
    @Setter
    private String currentPassword;

    @Getter
    @Setter
    private String newPassword;

    @Getter
    @Setter
    private String repeatedPassword;

    @Transactional
    public String login() //Boolean grazinti??
    {
        try{
            user.setPasswordHash(hashGenerator.generatePasswordHash(passwordHash));
            user = userDAO.login(user.getEmail(), user.getPasswordHash());
            if (user.getBlocked()) {
                Messages.addGlobalWarn("ERROR: You are blocked from system");
                return null;
            }
            //Messages.addGlobalWarn("Success");

            // ikelti vartotoja i home page
            return "home-page?faces-redirect=true";
        }
        catch (NoResultException ex)
        {
            //Nerado tokio vartotojo
            Messages.addGlobalWarn("Incorrect email or password");
            return null;
        }
    }

    @Transactional
    public String logout(){ //Boolean returninti?
        try{
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            // uzbaigti session scope: session.invalidate(); ???
            //user = null; //?
            return "index?faces-redirect=true";
        }
        catch (Exception ex){
            Messages.addGlobalWarn("FATAL ERROR: Could not log you out. Now you're stuck forever :(");
        }
        return null;
    }

    @Transactional
    public String changePassword(){
        try{
            if(!hashGenerator.generatePasswordHash(currentPassword).contentEquals(user.getPasswordHash())) {
                Messages.addGlobalInfo("Current password is not correct");
                return null;
            }

            if(!newPassword.contentEquals(repeatedPassword)) {
                Messages.addGlobalWarn("New and repeated password are not equal");
                return null;
            }
            currentPassword = newPassword;
            userDAO.changeUserPassword(user.getEmail(), hashGenerator.generatePasswordHash(newPassword));
            Messages.addGlobalInfo("Password was successfully changed");
            return "home-page?faces-redirect=true";
        }
        catch(Exception ex){
            Messages.addGlobalWarn("FATAL ERROR: User password change failed");
            return null;
        }
    }

}
