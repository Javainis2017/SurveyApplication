package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.User;
import lombok.Getter;
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
    @Getter // nieko blogo? AdminBlockController nuodojasi situo
    private UserDAO userDAO;

    @Transactional
    public String login() //Boolean grazinti??
    {
        try{
            user = userDAO.login(user.getEmail(), user.getPasswordHash());
            Messages.addGlobalWarn("Success");
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
        Messages.addGlobalWarn("Log out method");
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

}
