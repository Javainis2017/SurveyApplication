package com.javainis.utility;


import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class RandomStringGenerator {

    private Random random = new Random();

    private String chars = "0123456789abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generateString(int length){
        StringBuilder builder = new StringBuilder(length);
        for(int i = 0; i < length; i++){
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return builder.toString();
    }

}
