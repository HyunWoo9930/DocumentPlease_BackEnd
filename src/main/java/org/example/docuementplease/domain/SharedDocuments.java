package org.example.docuementplease.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class SharedDocuments {
    private String name;
    private Long user_id;
    private String target;
    private String content;
}
