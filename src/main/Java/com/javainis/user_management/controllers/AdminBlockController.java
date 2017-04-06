package com.javainis.user_management.controllers;


import com.javainis.user_management.entities.User;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
public class AdminBlockController {

    @Inject
    private UserController userController;

    @Transactional
    public Boolean changeUserBlockStatus(String email, Boolean blocked){
        if (!checkAdminRights()) return false;
        return userController.getUserDAO().changeBlockStatus(email, blocked);
    }

    @Transactional
    public List<User> getAllUser(){
        return userController.getUserDAO().getAllUsers();
    }

    private Boolean checkAdminRights(){
        return userController.getUser().getUserTypeID().getId() == 1; // 1 - Admin, 2 - User
    }

}
