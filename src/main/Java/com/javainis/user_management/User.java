package com.javainis.user_management;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User entity
 */
@Entity
@Table(name = "app_user")
@NamedQueries({
        @NamedQuery(name = "User.findUser", query = "SELECT u FROM User u WHERE u.email = :email AND u.passwordHash = :passwordHash"),
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

    private Boolean blocked;

    public void setPasswordHash(String password)
    {
        System.out.println("HASHING PASSWORD");
        passwordHash = password;
        System.out.println("COMPLETED");

    }
}
