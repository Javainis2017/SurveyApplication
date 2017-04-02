package com.javainis.user_management;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@ApplicationScoped
public class UserTypeDAO
{
    @Inject
    private EntityManager manager;

    public void create(UserType type) {
        manager.persist(type);
    }

    public UserType getUserTypeById(long id)
    {
        try {
            UserType result = manager.createNamedQuery("UserType.findType", UserType.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return result;
        }
        catch (Exception ex) //daugiau exception? Not found?
        {
            return null;
        }
    }
}
