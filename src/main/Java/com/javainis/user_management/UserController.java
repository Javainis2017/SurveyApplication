package com.javainis.user_management;

import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Model
public class UserController
{
    @Getter
    private User user = new User();

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserTypeDAO typeDAO;

    @Transactional
    public void login()
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
    public void register()
    {
        //Reikes pakeisti
        UserType type = new UserType();
        type.setName("User");
        user.setUserTypeID(type);
        typeDAO.create(type);
        userDAO.create(user);
    }
}
