package com.monopoly.service;

import com.monopoly.data.model.Player;
import com.monopoly.data.repository.PlayerRepository;
import com.monopoly.exception.InsufficientFundsException;
import com.monopoly.exception.InvalidGameActionException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanService {
    private final PlayerRepository playerRepository;

    @Transactional
    public long makeLoanPayment(Player player, long payment) {
        if (payment < 0) {
            throw new InvalidGameActionException("Loan payment cannot be negative.");
        }

        if (payment > player.getLoanBalance()) {
            throw new InvalidGameActionException("Cannot pay more than the remaining loan balance of "
                    + formatNaira(player.getLoanBalance()));
        }

        if (payment > player.getCashBalance()) {
            throw new InsufficientFundsException("Not enough cash. Available: "
                    + formatNaira(player.getCashBalance()) + ", Trying to pay: " + formatNaira(payment));
        }

        player.setCashBalance(player.getCashBalance() - payment);
        player.setLoanBalance(player.getLoanBalance() - payment);

        playerRepository.save(player);

        return player.getLoanBalance();
    }

    @Transactional
    public long applyInterest(Player player) {
        if (player.getLoanBalance() <= 0) {
            return 0L;
        }

        long interest = Math.round(player.getLoanBalance() * 0.10);
        long newBalance = player.getLoanBalance() + interest;

        player.setLoanBalance(newBalance);
        playerRepository.save(player);

        return newBalance;
    }

    public long previewInterest(long currentLoanBalance, long proposedPayment) {
        long balanceAfterPayment = currentLoanBalance - proposedPayment;
        if (balanceAfterPayment <= 0)
            return 0L;
        return Math.round(balanceAfterPayment * 0.10);
    }

    private String formatNaira(long amount) {
        return String.format("₦%,d", amount);
    }
}
