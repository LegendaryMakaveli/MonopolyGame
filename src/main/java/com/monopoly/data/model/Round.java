package com.monopoly.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rounds")
@Getter
@Setter
@NoArgsConstructor
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game game;
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    private RoundPhase phase = RoundPhase.HOUSING;

    private Boolean isCompleted = false;

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlayerRound> playerRounds = new ArrayList<>();
}
