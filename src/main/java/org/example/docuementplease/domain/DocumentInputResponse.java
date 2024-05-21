package org.example.docuementplease.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class DocumentInputResponse {
//    type, target, amount, text
    private String type;
    private String target;
    private int amount;
    private String text;
}
