package org.example.docuementplease.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class SharedDocuments {
    private String name;
    private String user_name;
    private String target;
    private String content;
    private int like_count;
    private String type;
    private List<Comment> comments;
}
