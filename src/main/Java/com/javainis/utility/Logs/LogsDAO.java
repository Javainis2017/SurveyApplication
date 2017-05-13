package com.javainis.utility.Logs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class LogsDAO {

    @Inject
    private EntityManager manager;

    public void createLog(Logs log){
        manager.persist(log);
    }
}
