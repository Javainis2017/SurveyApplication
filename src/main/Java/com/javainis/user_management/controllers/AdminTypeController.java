package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserTypeDAO;
import com.javainis.user_management.entities.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
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
        if (!isAdmin()) return false;

        return userController.getUserDAO().changeUserType(email, type);
    }

    public List<User> getAllUser(){
        return userController.getUserDAO().getAllUsers();
    }

    public String getUserTypeName(int type){
        return userTypeDAO.getUserTypeById(type).getName();
    }

    private Boolean isAdmin(){
        // 1 - Admin, 2 - User
        return userController.getUser().getUserTypeID().getId() == 1;
    }

}
