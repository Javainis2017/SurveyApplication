package com.javainis.user_management.controllers;

import com.javainis.utility.HashGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private HashGenerator hashGenerator;

    @Before
    public void setUp() throws Exception
    {
        userController = new UserController();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void login() throws Exception {
        hashGenerator.generatePasswordHash("a");

        userController.getUser().setEmail("a@a.lt");
        String message = hashGenerator.generatePasswordHash("a");
        userController.setPasswordHash("a");
        userController.getUser().setPasswordHash(message);
        userController.login();
        assertEquals(message, userController.getUser().getPasswordHash());
    }

}