package com.javainis.utility;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;

@ApplicationScoped
public class ExpirationChecker {
    public Boolean isExpired(Timestamp timestamp)
    {
        Timestamp currTimestamp = new Timestamp(System.currentTimeMillis());
        if(currTimestamp.before(timestamp))
            return false;

        return true;
    }
}
