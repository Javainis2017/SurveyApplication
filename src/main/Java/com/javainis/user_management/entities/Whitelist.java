package com.javainis.user_management.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@NamedQueries({
        @NamedQuery(name = "Whitelist.findEmail", query = "SELECT w FROM Whitelist w WHERE w.email = :email"),
        @NamedQuery(name = "Whitelist.getAll", query = "SELECT w FROM Whitelist w"),
        @NamedQuery(name = "Whitelist.remove", query = "DELETE FROM Whitelist w WHERE w.email = :email"),
})
@Getter
@Setter
public class Whitelist
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(max = 50)
    private String email;
}
