package com.javainis.user_management;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

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
        return manager.createNamedQuery("User.findUser", User.class)
                    .setParameter("email", email)
                    .setParameter("passwordHash", pwHash)
                    .getSingleResult();
    }
}
