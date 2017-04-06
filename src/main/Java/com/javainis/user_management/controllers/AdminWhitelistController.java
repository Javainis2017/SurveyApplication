package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.dao.UserTypeDAO;
import com.javainis.user_management.entities.Whitelist;
import com.javainis.user_management.dao.WhitelistDAO;
import com.javainis.user_management.entities.User;
import lombok.Getter;
import org.omnifaces.util.Messages;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class AdminWhitelistController{

    @Inject
    private UserController userController;

    @Getter
    private Whitelist whitelist = new Whitelist();

    @Inject
    private UserTypeDAO typeDAO;

    @Inject
    private WhitelistDAO whitelistDAO;

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

    @Transactional
    public Boolean removeFromWhitelist(String email){
        return checkAdminRights() && whitelistDAO.removeFromWhitelist(email) != 0;
    }

    private Boolean checkAdminRights(){
        return userController.getUser().getUserTypeID().getId() == 1; // 1 - Admin, 2 - User
    }

}
