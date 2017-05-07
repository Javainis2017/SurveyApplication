package com.javainis.utility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.sql.Timestamp;

@Named
@ApplicationScoped
public class ExpirationChecker {
    public Boolean isExpired(Timestamp timestamp)
    {
        if(timestamp == null)
            return false;

        Timestamp currTimestamp = new Timestamp(System.currentTimeMillis());
        if(currTimestamp.before(timestamp))
            return false;

        return true;
    }
}
