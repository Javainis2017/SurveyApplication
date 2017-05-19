package com.javainis.user_management.controllers;

import com.javainis.user_management.dao.UserDAO;
import com.javainis.user_management.entities.User;
import com.javainis.utility.HashGenerator;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.io.Serializable;

@Named
@ViewScoped
public class UserProfileController implements Serializable{

    @Inject
    private UserController userController;

    @Inject
    private UserDAO userDAO;

    @Inject
    private HashGenerator hashGenerator;

    @Getter
    private String currentPassword;

    @Getter
    private User user;

    @Getter
    @Setter
    private String newPassword;

    @Getter
    @Setter
    private String repeatedPassword;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    private User conflictingUser;

    @PostConstruct
    private void init(){
        userController.refreshUser();
        user = userController.getUser();
        firstName = user.getFirstName();
        lastName = user.getLastName();
    }

    @Transactional
    public void saveProfile(){
        try{
            resetPasswordFields();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userDAO.update(user);
            userController.refreshUser();
            user = userController.getUser();
            conflictingUser = null;
            FacesContext.getCurrentInstance().addMessage("profileMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User profile was successfully changed."));
        }catch (OptimisticLockException ole){
            conflictingUser = userDAO.findById(user.getUserID());
        }
    }

    @Transactional
    public void overwrite(){
        user.setOptLockVersion(conflictingUser.getOptLockVersion());
        saveProfile();
    }

    public void refresh(){
        user = userDAO.findById(user.getUserID());
        firstName = user.getFirstName();
        lastName = user.getLastName();
        conflictingUser = null;
    }

    @Transactional
    public void changePassword(){
        try{
            if(!currentPassword.contentEquals(user.getPasswordHash())) {
                Messages.addGlobalInfo("Current password is not correct");
                return;
            }

            if(!newPassword.contentEquals(repeatedPassword)) {
                Messages.addGlobalWarn("New and repeated password are not equal");
                return;
            }
            newPassword = hashGenerator.generatePasswordHash(newPassword);
            userDAO.changeUserPassword(user.getEmail(), newPassword);
            user.setPasswordHash(newPassword);
            userDAO.update(user);

            Messages.addGlobalInfo("Password was successfully changed");
            resetPasswordFields();
        }
        catch(Exception ex){
            ex.printStackTrace();
            Messages.addGlobalWarn("User password change failed");
            resetPasswordFields();
        }
    }

    public void setCurrentPassword(String password) {
        if(!password.isEmpty()){
            currentPassword = hashGenerator.generatePasswordHash(password);
        }
    }

    private void resetPasswordFields() {
        currentPassword = "";
        newPassword = "";
        repeatedPassword = "";
    }
}
