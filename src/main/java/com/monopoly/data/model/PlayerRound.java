package com.monopoly.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_rounds")
@Getter
@Setter
@NoArgsConstructor
public class PlayerRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    @JsonIgnore
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    @JsonIgnore
    private Round round;
    private Long salaryReceived;
    @Enumerated(EnumType.STRING)
    private HousingType housingType;
    private Long housingCostPaid;
    private Long survivalCost = 700_000L;
    private Long loanPayment;
    private Long loanBalanceAfter;
    private Integer diceRoll;
    @Enumerated(EnumType.STRING)
    private DiceEventType eventType;
    private Long eventAmount;
    private Long cashBalanceEnd;
    private Long netWorth;
    private Boolean isCompleted = false;
    private LocalDateTime completedAt;
}