package com.monopoly.service;

import com.monopoly.data.model.HousingOption;
import com.monopoly.data.model.HousingType;
import com.monopoly.data.repository.HousingOptionRepository;
import com.monopoly.exception.InvalidGameActionException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HousingService {
    private final HousingOptionRepository housingOptionRepository;

    public long calculateHousingCost(HousingType housingType, int roundNumber, int diceRoll) {
        HousingOption option = housingOptionRepository.findByHousingType(housingType)
                .orElseThrow(() -> new InvalidGameActionException("Housing option not found: " + housingType));
        long baseCost = option.getBaseCost();

        if (roundNumber == 1) {
            return baseCost;
        }

        if (housingType == HousingType.PARENT_HOUSE) {
            double inflationRate = option.getInflationRatePerDicePip() * diceRoll;
            double multiplier = Math.pow(1 + inflationRate, roundNumber - 1);
            return Math.round(baseCost * multiplier);
        }

        double multiplier = Math.pow(1 + option.getFixedRatePercent(), roundNumber - 1);
        return Math.round(baseCost * multiplier);
    }
}
