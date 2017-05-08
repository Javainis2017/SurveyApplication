package com.javainis.user_management.entities;

import com.javainis.utility.HashGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
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
@EqualsAndHashCode(of = "email")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userID;

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
    private UserType userType;

    private Boolean blocked = false;
}
