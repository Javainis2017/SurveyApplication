package com.javainis.user_management.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.MessageDigest;

/**
 * User entity
 */
@Entity
@Table(name = "app_user")
@NamedQueries({
        @NamedQuery(name = "User.findUser", query = "SELECT u FROM User u WHERE u.email = :email AND u.passwordHash = :passwordHash"),
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.findEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
})
@Getter
@Setter
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long userID;

    @Size(max = 50)
    @NotNull
    private String email;

    @Size(max = 64)
    @Column(name = "password_hash")
    @NotNull
    private String passwordHash;

    @Size(max = 30)
    @Column(name = "first_name")
    @NotNull
    private String firstName;

    @Size(max = 40)
    @Column(name = "last_name")
    @NotNull
    private String lastName;

    @JoinColumn(name = "user_type_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private UserType userTypeID;

    private Boolean blocked = false;

    public void setPasswordHash(String password)
    {
        //passwordHash = RandomStringGenerator.hash(password);
        try {
            System.out.println("HASHING PASSWORD");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes("UTF-8"));
            passwordHash = String.format("%064x", new java.math.BigInteger(1, digest.digest()));
            System.out.println("HASH COMPLETED: " + passwordHash);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
