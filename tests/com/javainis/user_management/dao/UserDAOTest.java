package com.javainis.user_management.dao;

import com.javainis.user_management.entities.User;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ignas on 2017-04-13.
 */
public class UserDAOTest {
    private UserDAO userDAO = new UserDAO();
    private User user = new User();

    @Test
    public void create() throws Exception {
        user.setEmail("c@c.lt");
        user.setPasswordHash("a");
        userDAO.create(user);
        User usertest = userDAO.getUserByEmail("c@c.lt");
        assertEquals("c@c.lt", usertest.getEmail());
    }

    @Test
    public void login() throws Exception {
    }

}