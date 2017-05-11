package com.javainis;

import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;
import javax.transaction.TransactionScoped;
import java.io.Serializable;

@ApplicationScoped
public class JPAResources implements Serializable
{
    @PersistenceUnit
    private EntityManagerFactory factory;

    @Produces
    @Default
    @RequestScoped
    private EntityManager createJTAEntityManager()
    {
        return factory.createEntityManager(SynchronizationType.SYNCHRONIZED);
    }

    @Produces
    @Async
    @TransactionScoped
    private EntityManager createJTATransactionalEM()
    {
        return factory.createEntityManager(SynchronizationType.SYNCHRONIZED);
    }


    private void closeUnsynchronizedEntityManager(@Disposes @Default EntityManager em)
    {
        em.close();
    }
    private void closeAsyncEntityManager(@Disposes @Async EntityManager em) {
        em.close();
    }
}
