package com.javainis.user_management;

import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Model
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
    public void login() //Boolean grazinti??
    {
        try{
            User loggedIn = userDAO.login(user.getEmail(), user.getPasswordHash());

            Messages.addGlobalWarn("Success");

            //Daryti kazka su prisijungusiu vartotoju
        }
        catch (NoResultException ex)
        {
            //Nerado tokio vartotojo
            Messages.addGlobalWarn("Incorrect email or password");
        }
    }

    @Transactional
    public void logout(){ //Boolean returninti?
        try{
            // uzbaigti session scope: session.invalidate(); ???


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
