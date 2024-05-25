package org.example.docuementplease.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.docuementplease.Listeners.PaymentListener;
import org.springframework.data.annotation.CreatedDate;


import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EntityListeners(PaymentListener.class)
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;
    private int ticket;
    private int price;

    @CreatedDate
    private LocalDateTime paid_time;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PaymentHistory() {
    }

}

