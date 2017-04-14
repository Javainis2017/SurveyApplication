package com.javainis.user_management.dao;

import com.javainis.user_management.entities.MailExpiration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@ApplicationScoped
public class MailExpirationDAO
{
    @Inject
    private EntityManager manager;

    public void create(MailExpiration mailExpiration) {
        manager.persist(mailExpiration);
    }

    public int removeFromMailExpiration(String token)
    {
        return manager.createNamedQuery("MailExpiration.remove")
                .setParameter("token", token)
                .executeUpdate();
    }

    public Boolean findMailExpiration(String token)
    {
        try {
            MailExpiration result = manager.createNamedQuery("MailExpiration.findToken", MailExpiration.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return result.getToken().equals(token);
        }
        catch(NoResultException ex)
        {
            return false;
        }
    }

    public List<MailExpiration> getAll()
    {
        return manager.createNamedQuery("MailExpiration.findAll", MailExpiration.class).getResultList();
    }
}
