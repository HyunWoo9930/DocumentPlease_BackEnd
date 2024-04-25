package org.example.docuementplease.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "username", "password", "nick_name" }) })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;
    private String username;
    private String password;
    private String email;
    private int tickets;
    private int daily_tickets;
    private String nick_name;

    @ManyToMany
    @JoinTable(
            name = "document_ids",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Documents> documents = new ArrayList<>();;

    public User() {
    }

}
