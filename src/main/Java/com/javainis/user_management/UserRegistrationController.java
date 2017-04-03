package com.javainis.user_management;

import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 * Created by Ignas on 2017-04-03.
 */
@Model
@Named
@RequestScoped //Request užtenka?
public class UserRegistrationController {

    @Getter
    private User user = new User();

    @Getter
    private Whitelist whitelist = new Whitelist();

    @Inject
    private WhitelistDAO whitelistDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserTypeDAO typeDAO;

    @Transactional
    public void register()
    {

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
            // typeDAO.create(type); //tipas paimtas is db, nereikia dar prideti
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
}
