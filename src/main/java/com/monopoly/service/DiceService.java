package com.monopoly.service;

import com.monopoly.data.model.*;
import com.monopoly.data.repository.DiceEventRepository;
import com.monopoly.data.repository.InvestmentRepository;
import com.monopoly.data.repository.PlayerRepository;
import com.monopoly.exception.InvalidGameActionException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.monopoly.util.Mapper.mapToGoodInvestment;
import static com.monopoly.util.Mapper.mapToInvestmentYield;

@Service
@AllArgsConstructor
public class DiceService {
    private final DiceEventRepository diceEventRepository;
    private final InvestmentRepository investmentRepository;
    private final PlayerRepository playerRepository;
    private final Random random = new Random();

    @Transactional
    public DiceRollResult rollAndApplyEvent(Player player, Game game) {
        int roll = random.nextInt(6) + 1;

        DiceEvent event = diceEventRepository.findByDiceValue(roll)
                .orElseThrow(() -> new InvalidGameActionException("No event configured for dice value: " + roll));

        long eventAmount = applyEvent(player, game, event);

        playerRepository.save(player);

        return new DiceRollResult(roll, event.getEventType(), event.getDescription(), eventAmount);
    }

    private long applyEvent(Player player, Game game, DiceEvent event) {
        return switch (event.getEventType()) {

            case JOB_LOSS -> {
                player.setMissNextSalary(true);
                player.setStatus(PlayerStatus.JOB_LOST);
                yield 0L;
            }

            case MEDICAL_EMERGENCY, FAMILY_EMERGENCY -> {
                long cost = event.getAmount();
                player.setCashBalance(player.getCashBalance() - cost);
                yield -cost;
            }

            case FAMILY_SUPPORT -> {
                long income = event.getAmount();
                player.setCashBalance(player.getCashBalance() + income);
                yield income;
            }

            case GOOD_INVESTMENT -> {
                long cost = event.getAmount();
                player.setCashBalance(player.getCashBalance() - cost);

                Investment investment = new Investment();
                mapToGoodInvestment(player, game, event, investment, cost);
                investmentRepository.save(investment);

                yield -cost;
            }

            case BAD_INVESTMENT -> {
                long cost = event.getAmount();
                player.setCashBalance(player.getCashBalance() - cost);

                Investment investment = new Investment();
                mapToInvestmentYield(player, game, event, investment, cost);
                investmentRepository.save(investment);

                yield -cost;
            }
        };
    }

    public record DiceRollResult(
            int diceRoll,
            DiceEventType eventType,
            String eventDescription,
            long eventAmount) {
    }
}
