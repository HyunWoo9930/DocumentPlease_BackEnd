package org.example.docuementplease.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class PaymentHistoryResponse {
    private LocalDateTime paidTime;
    private int ticket;
    private int price;
}
