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
public class Documents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String type;
    private String content;
    private String target;
    private int amount;
    private String text;

    @ManyToMany(mappedBy = "documents")
    private List<User> users = new ArrayList<>();;

    public Documents() {
    }
}
