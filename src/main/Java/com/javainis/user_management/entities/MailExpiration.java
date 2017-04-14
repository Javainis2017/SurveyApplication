package com.javainis.user_management.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "mail_expiration")
@NamedQueries({
        @NamedQuery(name = "MailExpiration.findToken", query = "SELECT u FROM MailExpiration u WHERE u.token = :token"),
        @NamedQuery(name = "MailExpiration.findAll", query = "SELECT u FROM MailExpiration u"),
        @NamedQuery(name = "MailExpiration.remove", query = "DELETE FROM MailExpiration u WHERE u.token = :token"),
})

@Getter
@Setter
public class MailExpiration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long mailID;

    @Size(max = 50)
    @NotNull
    private String email;

    @Size(max = 64)
    @NotNull
    private String token;

    @Column(name = "expiration_date")
    @NotNull
    private Date expirationDate;
}
