package com.javainis.utility.Logs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class LogDAO {

    @Inject
    private EntityManager manager;

    public void createLog(Log log){
        manager.persist(log);
    }
}
