package org.example.docuementplease.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "password", "nick_name"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;
    private String username;
    private String password;
    private String email;
    private int freeTickets;
    private int paidTickets;
    @Schema(hidden = true)
    private int daily_tickets = 5;
    private String nickName;
    private String profileUrl;
    private int document_create_count;
    private int totalLikes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Schema(hidden = true)
    private List<Level> levels = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Schema(hidden = true)
    private List<LikeLevel> likeLevels = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<TicketHistory> ticketHistory = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<Documents> documents = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<PaymentHistory> paymentHistory = new ArrayList<>();

    public User() {
        levels.add(Level.LEVEL1);
        levels.add(Level.LEVEL2);
        levels.add(Level.LEVEL3);
        levels.add(Level.LEVEL4);
        levels.add(Level.LEVEL5);
        likeLevels.add(LikeLevel.LEVEL1);
        likeLevels.add(LikeLevel.LEVEL2);
        likeLevels.add(LikeLevel.LEVEL3);
    }

    public void addLevel(Level level) {
        if (!levels.contains(level)) {
            levels.add(level);
        }
    }

    public void addLikeLevel(LikeLevel likeLevel) {
        if (!likeLevels.contains(likeLevel)) {
            likeLevels.add(likeLevel);
        }
    }

    public void removeLevel(Level level) {
        levels.remove(level);
    }

    public void removeLikeLevel(LikeLevel likeLevel) {
        likeLevels.remove(likeLevel);
    }

    public boolean hasLevel(Level level) {
        return levels.contains(level);
    }
    public boolean hasLikeLevel(LikeLevel likeLevel) {
        return likeLevels.contains(likeLevel);
    }
}
