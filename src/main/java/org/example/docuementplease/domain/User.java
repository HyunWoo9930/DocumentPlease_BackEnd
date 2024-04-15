package org.example.docuementplease.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "username", "password", "nick_name" }) })
public class User {
    // getter 및 setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true) // Swagger 문서에서 숨김
    private Long id;
    private String username;
    private String password;
    private String email;
    private int tickets;
    private int daily_tickets;
    private String nick_name;


    public User() {
    }

}

