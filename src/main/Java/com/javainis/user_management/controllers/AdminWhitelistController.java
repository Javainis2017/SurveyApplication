package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserTypeDAO;
import com.javainis.user_management.dao.WhitelistDAO;
import com.javainis.user_management.entities.Whitelist;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;

@Named
@RequestScoped
public class AdminWhitelistController{

    @Inject
    private UserController userController;

    @Getter
    private Whitelist whitelist = new Whitelist();

    @Getter
    @Setter
    private Whitelist selectedEmail;

    @Inject
    private UserTypeDAO typeDAO;

    @Inject
    @Getter
    private WhitelistDAO whitelistDAO;

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

    public List<Whitelist> getAllWhitelist()
    {
        return whitelistDAO.getAll();
    }

    @Transactional
    public Boolean removeFromWhitelist(String email)
    {
        return isAdmin() && whitelistDAO.removeFromWhitelist(email) != 0;
//        Messages.addGlobalWarn("selectedEmail is null");
//        return false;
    }

    private Boolean isAdmin(){
        // 1 - Admin, 2 - User
        return userController.getUser().getUserTypeID().getId() == 1;
    }

}
