package com.monopoly.service;


import com.monopoly.data.model.DiceEvent;
import com.monopoly.data.model.DiceEventType;
import com.monopoly.data.model.HousingOption;
import com.monopoly.data.model.HousingType;
import com.monopoly.data.repository.DiceEventRepository;
import com.monopoly.data.repository.HousingOptionRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.monopoly.util.Mapper.*;


@Service
@AllArgsConstructor
public class DataSeeder {
    private final HousingOptionRepository housingOptionRepository;
    private final DiceEventRepository diceEventRepository;

    @PostConstruct
    public void seed() {
        try {
            if (housingOptionRepository.count() == 0) {
                seedHousingOptions();
            }
            if (diceEventRepository.count() == 0) {
                seedDiceEvents();
            }
        } catch (Exception e) {
            seedHousingOptions();
            seedDiceEvents();
        }
    }

    private void seedHousingOptions() {
        if (housingOptionRepository.count() > 0) return;

        HousingOption parents = new HousingOption();
        mapToParentHousingOption(parents);

        HousingOption shared = new HousingOption();
        mapToSharedApartmentHousingOption(shared);

        HousingOption single = new HousingOption();
        mapToSingleHousingOption(single);

        HousingOption luxury = new HousingOption();
        mapToLuxuryApartmentHousingOption(luxury);

        housingOptionRepository.save(parents);
        housingOptionRepository.save(shared);
        housingOptionRepository.save(single);
        housingOptionRepository.save(luxury);
    }


    private void seedDiceEvents() {
        if (diceEventRepository.count() > 0) return;

        DiceEvent jobLoss = new DiceEvent();
        mapToJobLoss(jobLoss);

        DiceEvent medical = new DiceEvent();
        mapToMedical(medical);

        DiceEvent family = new DiceEvent();
        mapToFamily(family);

        DiceEvent support = new DiceEvent();
        mapToFamilySupport(support);

        DiceEvent goodInvestment = new DiceEvent();
        mapToGoodInvestment(goodInvestment);

        DiceEvent badInvestment = new DiceEvent();
        mapToBadInvestment(badInvestment);

        diceEventRepository.save(jobLoss);
        diceEventRepository.save(medical);
        diceEventRepository.save(family);
        diceEventRepository.save(support);
        diceEventRepository.save(goodInvestment);
        diceEventRepository.save(badInvestment);
    }
}
