package com.javainis.user_management.dao;

import com.javainis.user_management.entities.MailExpiration;
import com.javainis.user_management.entities.User;

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

    public int removeFromMailExpiration(User user)
    {
        return manager.createNamedQuery("MailExpiration.remove")
                .setParameter("user", user)
                .executeUpdate();
    }

    public MailExpiration findMailExpiration(String url)
    {
        try {
            MailExpiration result = manager.createNamedQuery("MailExpiration.findUrl", MailExpiration.class)
                    .setParameter("url", url)
                    .getSingleResult();

            return result;
        }
        catch(NoResultException ex)
        {
            return null;
        }
    }

    public List<MailExpiration> getAll()
    {
        return manager.createNamedQuery("MailExpiration.findAll", MailExpiration.class).getResultList();
    }
}
