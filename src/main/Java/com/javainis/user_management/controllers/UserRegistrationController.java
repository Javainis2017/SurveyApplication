package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.dao.UserTypeDAO;
import com.javainis.user_management.entities.Whitelist;
import com.javainis.user_management.dao.WhitelistDAO;
import com.javainis.user_management.entities.User;
import com.javainis.user_management.entities.UserType;
import com.javainis.utility.HashGenerator;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@RequestScoped //Request užtenka?
public class UserRegistrationController {

    @Getter
    private User user = new User();

    @Getter
    private Whitelist whitelist = new Whitelist();

    @Getter
    @Setter
    private String passwordHash;

    @Inject
    private WhitelistDAO whitelistDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserTypeDAO typeDAO;

    @Inject
    private HashGenerator hashGenerator;

    @Transactional
    public String register()
    {
        UserType type = typeDAO.getUserTypeById(2); // 1-Admin, 2-User
        user.setUserType(type);
        //Ar toks email jau uzregistruotas
        if (userDAO.emailIsRegistered(user.getEmail()))
        {
            Messages.addGlobalWarn("This email is already registered");
            return null;
        }
        //Ar email yra whitelist sarase
        else if (!whitelistDAO.findEmail(user.getEmail()))
        {
            Messages.addGlobalWarn("This email is not in whitelist.");
            return null;
        }
        else
        {
            user.setPasswordHash(hashGenerator.generatePasswordHash(passwordHash));
            userDAO.create(user);
            return "login?faces-redirect=true";
        }
    }

    @Transactional
    public void whitelistEmail()
    {
        if (!whitelistDAO.findEmail(whitelist.getEmail()))
        {
            whitelistDAO.create(whitelist);
            Messages.addGlobalInfo("Success");
        }
        else
        {
            Messages.addGlobalWarn("Email is already in whitelist");
        }
    }
}
