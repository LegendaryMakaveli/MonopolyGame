package com.monopoly.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dice_events")
@Getter
@Setter
@NoArgsConstructor
public class DiceEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Integer diceValue;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiceEventType eventType;
    private String title;
    private String description;
    private Long amount;
    private Long returnAmount;
    private Boolean isSpreadReturn;
}
