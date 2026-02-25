package com.monopoly.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;


@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game game;

    @Enumerated(EnumType.STRING)
    private PlayerStatus status = PlayerStatus.ACTIVE;
    private Long cashBalanceKobo = 240_000_000L;
    private Long loanBalanceKobo = 150_000_000L;
    private Long creditScore = 500L;
    private Long monthlySalaryKobo = 40_000_000L;
    @Enumerated(EnumType.STRING)
    private HousingType housingType;
    private Boolean missNextSalary = false;
    private Integer turnOrder;
    private Long finalNetWorthKobo;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlayerRound> playerRounds = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Investment> investments = new ArrayList<>();
}

