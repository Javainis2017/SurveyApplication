package com.javainis.utility;

import lombok.experimental.var;

import javax.enterprise.context.ApplicationScoped;
import java.security.MessageDigest;

@ApplicationScoped
public class HashGenerator {

    public static String generatePasswordHash(String password)
    {
        String hash = "";
        try {
            System.out.println("HASHING PASSWORD");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes("UTF-8"));
            hash = String.format("%064x", new java.math.BigInteger(1, digest.digest()));
            System.out.println("HASH COMPLETED: " + hash);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return hash;
    }
}
