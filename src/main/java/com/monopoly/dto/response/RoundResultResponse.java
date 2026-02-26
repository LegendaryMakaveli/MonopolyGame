package com.monopoly.dto.response;

import com.monopoly.data.model.DiceEventType;
import com.monopoly.data.model.HousingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class RoundResultResponse {
    private Long playerId;
    private String playerName;
    private Integer roundNumber;

    private Long salaryReceived;
    private HousingType housingType;
    private Long housingCost;
    private Long survivalCost;
    private Long loanPayment;
    private Long loanBalanceRemaining;

    private Integer diceRoll;
    private DiceEventType eventType;
    private String eventDescription;
    private Long eventAmount;

    private Long cashBalanceEnd;
    private Long netWorth;





    public String getFormattedNetWorth() {
        if (netWorth == null) return "₦0";
        return String.format("₦%,d", netWorth);
    }

    public String getFormattedCashBalance() {
        if (cashBalanceEnd == null) return "₦0";
        return String.format("₦%,d", cashBalanceEnd);
    }
}
