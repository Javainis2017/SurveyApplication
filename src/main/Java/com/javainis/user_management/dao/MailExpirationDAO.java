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

    public int removeFromMailExpiration(String url)
    {
        return manager.createNamedQuery("MailExpiration.remove")
                .setParameter("url", url)
                .executeUpdate();
    }

    public Boolean findMailExpiration(String url)
    {
        try {
            MailExpiration result = manager.createNamedQuery("MailExpiration.findUrl", MailExpiration.class)
                    .setParameter("url", url)
                    .getSingleResult();
            return result.getUrl().equals(url);
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
