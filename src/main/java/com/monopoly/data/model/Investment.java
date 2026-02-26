package com.monopoly.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "investments")
@Getter
@Setter
@NoArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    @JsonIgnore
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game game;

    @Enumerated(EnumType.STRING)
    private DiceEventType investmentType;
    private Integer investedInRound;
    private Long amountInvested;
    private Long totalReturn;
    private Long totalPaidOut = 0L;
    private Boolean isFullyPaidOut = false;
}
