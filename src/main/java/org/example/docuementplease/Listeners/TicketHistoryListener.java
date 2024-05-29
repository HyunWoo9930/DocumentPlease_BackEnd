package org.example.docuementplease.Listeners;

import jakarta.persistence.PrePersist;
import org.example.docuementplease.domain.TicketHistory;

import java.time.LocalDateTime;

public class TicketHistoryListener {

    @PrePersist
    public void prePersist(TicketHistory ticketHistory) {
        LocalDateTime now = LocalDateTime.now();
        ticketHistory.setCreatedAt(now);
    }
}
