package org.example.docuementplease.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "password", "nick_name"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;
    private String username;
    private String password;
    private String email;
    private int free_tickets;
    private int paid_tickets;
    @Schema(hidden = true)
    private int daily_tickets = 5;
    private String nick_name;
    private String profileUrl;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<Documents> documents = new ArrayList<>();

    public User() {
    }

}
