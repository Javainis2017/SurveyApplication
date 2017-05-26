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
        String result = validateEmail(whitelist.getEmail());
        if(result != null){
            Messages.addGlobalWarn(result);
        }
        else if (!whitelistDAO.findEmail(whitelist.getEmail()))
        {
            whitelistDAO.create(whitelist);
            Messages.addGlobalInfo("Success");
        }
        else
        {
            Messages.addGlobalWarn("Email is already in whitelist");
        }
    }

    private String validateEmail(String email)
    {
        if(!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
            return "Invalid email";
        }
        return null;
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
        return userController.getUser().getUserType().getId() == UserTypeDAO.USER_TYPE_ADMIN;
    }

}
