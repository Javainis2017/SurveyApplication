package com.javainis.utility;

import javax.enterprise.context.ApplicationScoped;
import java.security.MessageDigest;

@ApplicationScoped
public class HashGenerator {

    public String generatePasswordHash(String password)
    {
        String hash = password;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes("UTF-8"));
            hash = String.format("%064x", new java.math.BigInteger(1, digest.digest()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return hash;
    }
}
