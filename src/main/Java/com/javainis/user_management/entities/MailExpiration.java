package com.javainis.user_management.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "mail_expiration")
@NamedQueries({
        @NamedQuery(name = "MailExpiration.findUrl", query = "SELECT u FROM MailExpiration u WHERE u.url = :url"),
        @NamedQuery(name = "MailExpiration.findAll", query = "SELECT u FROM MailExpiration u"),
        @NamedQuery(name = "MailExpiration.remove", query = "DELETE FROM MailExpiration u WHERE u.user = :user"),
        @NamedQuery(name = "MailExpiration.existsByUrl", query = "SELECT COUNT(s) FROM MailExpiration s WHERE s.url = :url ")
})

@Getter
@Setter
public class MailExpiration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long mailID;

    @Size(max = 64)
    @NotNull
    private String url;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User user;
}
