package org.example.docuementplease.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.docuementplease.Listeners.RefundedDocumentListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@EntityListeners(RefundedDocumentListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RefundedDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;
    private String content;
    private String sendContent;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @JsonProperty("user_id")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public RefundedDocument() {
    }
}