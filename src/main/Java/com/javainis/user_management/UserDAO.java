package com.javainis.user_management;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * Created by Laurynas on 2017-03-30.
 */
@ApplicationScoped
public class UserDAO
{
    @Inject
    private EntityManager manager;

    public void create(User user) {
        manager.persist(user);
    }

    public User login(String email, String pwHash)
    {
        try {
            return manager.createNamedQuery("User.findUser", User.class)
                    .setParameter("email", email)
                    .setParameter("passwordHash", pwHash)
                    .getSingleResult();
        }
        catch (NoResultException ex)
        {
            throw ex;
        }
    }
}
