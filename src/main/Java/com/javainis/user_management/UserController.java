package com.javainis.user_management;

import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Model
public class UserController
{
    @Getter
    private User user = new User();

    @Getter
    private Whitelist whitelist = new Whitelist();

    @Inject
    private UserDAO userDAO;

    @Inject
    private WhitelistDAO whitelistDAO;

    @Inject
    private UserTypeDAO typeDAO;

    @Transactional
    public void login()
    {
        try{
            User loggedIn = userDAO.login(user.getEmail(), user.getPasswordHash());
            Messages.addGlobalWarn("Success");

            //Daryti kazka su prisijungusiu vartotoju
            //SessionScope
        }
        catch (NoResultException ex)
        {
            //Nerado tokio vartotojo
            Messages.addGlobalWarn("Incorrect email or password");
        }
    }

    @Transactional
    public void logout(){ //Ar Boolean returninti?
        try{
            // uzbaigti session scope
            //

        }
        catch (Exception ex){
            //kas blogai logout gali nutikti?
        }
    }

    @Transactional
    public void register()
    {
        //Reikes pakeisti
        //Kolkas nera skirtingu vartotoju tipu

        /*UserType type = new UserType();
        type.setName("User");*/
        UserType type = typeDAO.getUserTypeById(2); // 1-Admin, 2-User
        user.setUserTypeID(type);
        //Ar toks email jau uzregistruotas
        if (userDAO.emailIsRegistered(user.getEmail()))
        {
            Messages.addGlobalWarn("This email is already registered");

        }
        //Ar email yra whitelist sarase
        else if (!whitelistDAO.findEmail(user.getEmail()))
        {
            Messages.addGlobalWarn("This email is not included in whitelist");
        }
        else
        {
            // typeDAO.create(type);
            userDAO.create(user);
            Messages.addGlobalWarn("Success");
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


}
