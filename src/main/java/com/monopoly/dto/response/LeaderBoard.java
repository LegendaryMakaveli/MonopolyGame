package com.monopoly.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LeaderBoard {
    private Integer roundNumber;
    private List<PlayerStanding> standings;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlayerStanding {
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
}
