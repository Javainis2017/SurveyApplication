package com.javainis.user_management;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class UserTypeDAO
{
    @Inject
    private EntityManager manager;

    public void create(UserType type) {
        manager.persist(type);
    }
}
