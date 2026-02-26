package com.monopoly.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerStanding {
    private Integer rank;
    private Long playerId;
    private String playerName;
    private Long netWorth;
    private Long cashBalance;
    private Long loanBalance;

    public String getFormattedNetWorth() {
        return String.format("₦%,d", netWorth);
    }
}
