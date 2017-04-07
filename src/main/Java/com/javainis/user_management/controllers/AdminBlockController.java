package com.javainis.user_management.controllers;


import com.javainis.user_management.entities.User;
import org.omnifaces.util.Messages;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;

@Named
@RequestScoped
public class AdminBlockController {

    @Inject
    private UserController userController;

    @Transactional
    //Jeigu neuzblokuotas - uzblokuoti, jegu uzblokuotas - atblokuoti
    public Boolean changeUserBlockStatus(String email){
        if (!isAdmin()) return false;
        else if (userController.getUser().getEmail().equals(email))
        {
            //Neleidziama saves blokuoti
            Messages.addGlobalWarn("ERROR: You cannot block yourself");
            return false;
        }
        return userController.getUserDAO().changeBlockStatus(email);
    }

    @Transactional
    public List<User> getAllUser(){
        return userController.getUserDAO().getAllUsers();
    }

    private Boolean isAdmin(){
        // 1 - Admin, 2 - User
        return userController.getUser().getUserTypeID().getId() == 1;
    }

    public String getBlockedButtonLabel(Boolean blocked)
    {
        if(blocked){
            return "Unblock";
        }
        return "Block";
    }

}
