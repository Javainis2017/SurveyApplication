package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.dao.UserTypeDAO;
import com.javainis.user_management.entities.Whitelist;
import com.javainis.user_management.dao.WhitelistDAO;
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
public class AdminTypeController {

    @Inject
    private UserController userController;

    @Inject
    private UserTypeDAO userTypeDAO;

    @Transactional
    public Boolean changeUserType(String email, long type){
        if (!checkAdminRights()) return false;

        return userController.getUserDAO().changeUserType(email, type);
    }

    public List<User> getAllUser(){
        return userController.getUserDAO().getAllUsers();
    }

    public String getUserTypeName(int type){
        return userTypeDAO.getUserTypeById(type).getName();
    }

    private Boolean checkAdminRights(){
        return userController.getUser().getUserTypeID().getId() == 1; // 1 - Admin, 2 - User
    }

}
