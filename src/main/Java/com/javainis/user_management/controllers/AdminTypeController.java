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
        if(userController.getUser().getEmail().equals(email)) return false;
        return userController.getUserDAO().changeUserType(email, type);
    }

    @Transactional
    public Boolean toggleUserType(String email){
        if (!isAdmin()) return false;
        if(userController.getUser().getEmail().equals(email)) return false;
        if(userController.getUserDAO().getUserByEmail(email).getUserTypeID().getId() == 1){
            return userController.getUserDAO().changeUserType(email, 2); //1-admin 2-user
        }
        else { //from user to admin
            return userController.getUserDAO().changeUserType(email, 1);
        }
    }

    public String getUserTypeName(int type){
        return userTypeDAO.getUserTypeById(type).getName();
    }

    private Boolean isAdmin(){
        // 1 - Admin, 2 - User
        return userController.getUser().getUserTypeID().getId() == UserTypeDAO.USER_TYPE_ADMIN;
    }


    public String getTypeButtonLabel(long typeID)
    {
        if(typeID == 1){
            return "to User";
        }
        return "to Admin";
    }
}
