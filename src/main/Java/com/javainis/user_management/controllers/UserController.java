package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.dao.UserTypeDAO;
import com.javainis.user_management.entities.Whitelist;
import com.javainis.user_management.dao.WhitelistDAO;
import com.javainis.user_management.entities.User;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class UserController implements Serializable
{
    @Getter
    private User user = new User();

    @Inject
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
    public void logout(){ //Boolean returninti?
        try{
            // uzbaigti session scope: session.invalidate(); ???
            user = null; //?
        }
        catch (Exception ex){
            //kas blogai logout gali nutikti?
        }
    }

}
