package org.example.docuementplease.Listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.example.docuementplease.domain.Documents;

import java.time.LocalDateTime;

public class DocumentsListener {

    @PrePersist
    public void prePersist(Documents documents) {
        LocalDateTime now = LocalDateTime.now();
        documents.setCreatedAt(now);
        documents.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(Documents documents) {
        documents.setUpdatedAt(LocalDateTime.now());
    }
}