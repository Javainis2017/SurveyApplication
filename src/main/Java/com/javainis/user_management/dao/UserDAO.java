package com.javainis.user_management.dao;

import com.javainis.user_management.entities.User;
import com.javainis.user_management.entities.UserType;

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

    public Boolean changeUserType(String email, long typeID)
    {
        try{
            User user = manager.createNamedQuery("User.findEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            UserType userType = manager.createNamedQuery("UserType.findType", UserType.class)
                    .setParameter("id", typeID)
                    .getSingleResult();
            if (user.getUserTypeID().getId() == typeID) return false; // nesikeicia type
            user.setUserTypeID(userType);
            manager.persist(user); //nereikia?
            return true;
        }
        catch (NoResultException ex){ //kokie exception?
            return false;
        }
    }

    public Boolean changeBlockStatus(String email, Boolean blocked){
        try{
            User user = manager.createNamedQuery("User.findEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (user.getBlocked() == blocked) return false; //nebuvo atliktas pakeitimas
            user.setBlocked(blocked);
            manager.persist(user); //nereikia?
            return true;
        }
        catch (NoResultException ex){
            return false;
        }
    }

    public Boolean changeBlockStatus(String email){
        try{
            User user = manager.createNamedQuery("User.findEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            //Jeigu neuzblokuotas - uzblokuoti, jegu uzblokuotas - atblokuoti
            if (user.getBlocked() == null)
            {
                user.setBlocked(true);
            }
            else {
                user.setBlocked(!user.getBlocked());
            }
            return true;
        }
        catch (NoResultException ex){
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
