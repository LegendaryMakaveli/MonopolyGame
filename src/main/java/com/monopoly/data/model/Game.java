package com.monopoly.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "games")
@Setter
@Getter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String gameCode;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;

    @Column
    private Integer currentRound = 0;

    @Column
    private Integer totalRounds = 4;

    @Enumerated(EnumType.STRING)
    private RoundPhase currentPhase = RoundPhase.HOUSING;

    @Column
    private Integer maxPlayers = 3;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column
    private LocalDateTime startedAt;
    @Column
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds = new ArrayList<>();
}