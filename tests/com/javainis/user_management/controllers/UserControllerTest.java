package com.javainis.user_management.controllers;

import com.javainis.user_management.entities.User;
import org.junit.Test;
import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Created by Ignas on 2017-04-13.
 */
public class UserControllerTest {


    private UserController userController = new UserController();

    @Test
    public void login() throws Exception {
        userController.getUser().setEmail("a@a.lt");
        userController.setPasswordHash("a");
        userController.login();
        assertEquals("a@a.lt", userController.getUser().getEmail());

    }

}