package org.example.docuementplease.Listeners;

import jakarta.persistence.PrePersist;
import org.example.docuementplease.domain.RefundedDocument;

import java.time.LocalDateTime;

public class RefundedDocumentListener {

    @PrePersist
    public void prePersist(RefundedDocument refundedDocument) {
        LocalDateTime now = LocalDateTime.now();
        refundedDocument.setCreatedAt(now);
    }
}
