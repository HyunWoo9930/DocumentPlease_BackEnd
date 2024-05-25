package org.example.docuementplease.Listeners;

import jakarta.persistence.PrePersist;
import org.example.docuementplease.domain.PaymentHistory;

import java.time.LocalDateTime;

public class PaymentListener {

    @PrePersist
    public void prePersist(PaymentHistory paymentHistory) {
        LocalDateTime now = LocalDateTime.now();
        paymentHistory.setPaid_time(now);
    }
}
