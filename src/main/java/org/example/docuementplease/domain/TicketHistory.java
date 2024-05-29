package org.example.docuementplease.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.docuementplease.Listeners.TicketHistoryListener;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(TicketHistoryListener.class)
public class TicketHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // user_id만 반환하는 커스텀 getter
    @JsonProperty("user_id")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public TicketHistory() {
    }

}
