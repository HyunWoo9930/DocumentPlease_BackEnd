package org.example.docuementplease.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.docuementplease.Listeners.CommentListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(CommentListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;
    private String content;
    private String userName;
    private int likeCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "documents_id")
    @JsonIgnore
    private Documents documents;


}
