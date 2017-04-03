package com.javainis.user_management.dao;

import com.javainis.user_management.entities.Whitelist;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@ApplicationScoped
public class WhitelistDAO
{
    @Inject
    private EntityManager manager;

    public void create(Whitelist record) {
        manager.persist(record);
    }

    public int removeFromWhitelist(String email)
    {
        return manager.createNamedQuery("Whitelist.remove").executeUpdate();
    }

    public Boolean findEmail(String email)
    {
        try {
            Whitelist result = manager.createNamedQuery("Whitelist.findEmail", Whitelist.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return result.getEmail().equals(email);
        }
        catch(NoResultException ex)
        {
            return false;
        }
    }

    public List<Whitelist> getAll()
    {
        return manager.createNamedQuery("Whitelist.getAll", Whitelist.class).getResultList();
    }

}
