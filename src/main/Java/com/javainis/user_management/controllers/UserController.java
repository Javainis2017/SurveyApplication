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

    @Getter
    private Whitelist whitelist = new Whitelist();

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserTypeDAO typeDAO;

    @Inject
    private WhitelistDAO whitelistDAO; //admin operacijoms gali prireikti

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
            return "login-page?faces-redirect=true";
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

    @Transactional
    public void whitelistEmail()
    {
        if (!whitelistDAO.findEmail(whitelist.getEmail()))
        {
            whitelistDAO.create(whitelist);
            Messages.addGlobalWarn("Success");
        }
        else
        {
            Messages.addGlobalWarn("Email is already in whitelist");
        }
    }

    public List<Whitelist> getAllWhitelist()
    {
        return whitelistDAO.getAll();
    }

    public Boolean addToWhiteList(String email){
        if (!checkAdminRights()) return false;
        Whitelist record = new Whitelist();
        record.setEmail(email);
        whitelistDAO.create(record); // galetu ne void grazinti
        return true;
    }

    public Boolean removeFromWhitelist(String email){
         return checkAdminRights() && whitelistDAO.removeFromWhitelist(email) != 0;
    }

    private Boolean checkAdminRights(){
        return user.getUserTypeID().getId() == 1; // 1 - Admin, 2 - User
    }

}
