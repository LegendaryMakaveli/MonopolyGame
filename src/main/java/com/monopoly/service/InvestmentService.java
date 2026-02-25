package com.monopoly.service;

import com.monopoly.data.model.DiceEventType;
import com.monopoly.data.model.Investment;
import com.monopoly.data.model.Player;
import com.monopoly.data.repository.InvestmentRepository;
import com.monopoly.data.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public long payoutPendingInvestments(Player player, int currentRound, int totalRounds) {
        List<Investment> pending = investmentRepository.findByPlayerIdAndIsFullyPaidOut(player.getId(), false);

        long totalPaidOut = 0L;

        for (Investment investment : pending) {
            long payout = calculatePayout(investment, currentRound, totalRounds);

            if (payout > 0) {
                player.setCashBalanceKobo(player.getCashBalanceKobo() + payout);
                investment.setTotalPaidOutKobo(investment.getTotalPaidOutKobo() + payout);
                if (investment.getTotalPaidOutKobo() >= investment.getTotalReturnKobo()) {
                    investment.setIsFullyPaidOut(true);
                }

                investmentRepository.save(investment);
                totalPaidOut += payout;
            }
        }

        if (totalPaidOut > 0) {
            playerRepository.save(player);
        }

        return totalPaidOut;
    }

    private long calculatePayout(Investment investment, int currentRound, int totalRounds) {
        int roundInvested = investment.getInvestedInRound();

        if (investment.getInvestmentType() == DiceEventType.GOOD_INVESTMENT) {
            boolean isNextRound = currentRound == roundInvested + 1;
            if (isNextRound && !investment.getIsFullyPaidOut()) {
                return investment.getTotalReturnKobo();
            }
            return 0L;
        }

        if (investment.getInvestmentType() == DiceEventType.BAD_INVESTMENT) {
            int remainingRoundsAtTimeOfInvestment = totalRounds - roundInvested;
            if (remainingRoundsAtTimeOfInvestment <= 0)
                return 0L;

            boolean isAfterInvestment = currentRound > roundInvested;
            if (isAfterInvestment && !investment.getIsFullyPaidOut()) {
                long perRoundPayout = investment.getTotalReturnKobo() / remainingRoundsAtTimeOfInvestment;
                boolean isLastRound = currentRound == totalRounds;
                if (isLastRound) {
                    return investment.getTotalReturnKobo() - investment.getTotalPaidOutKobo();
                }
                return perRoundPayout;
            }
        }
        return 0L;
    }

    public long calculateTotalInvestmentValue(Player player) {
        List<Investment> active = investmentRepository.findByPlayerIdAndIsFullyPaidOut(player.getId(), false);
        return active.stream()
                .mapToLong(inv -> inv.getTotalReturnKobo() - inv.getTotalPaidOutKobo())
                .sum();
    }
}
