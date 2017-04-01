package com.javainis.user_management;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

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

    public List<User> getAllUsers()
    {
        return manager.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public Boolean changeUserType(User user, int type)
    {
        try{
            //reikia userType
            //Update sakini irgi named query laikyti?


            return true;
        }
        catch (Exception ex){ //kokie exception?

            return false;
        }
    }

    public Boolean emailIsRegistered(String email)
    {
        try {
            User result = manager.createNamedQuery("User.findEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return result != null;
        }
        catch (NoResultException ex)
        {
            return false;
        }
    }
}
