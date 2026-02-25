package com.monopoly.util;

import com.monopoly.data.model.*;
import com.monopoly.data.repository.PlayerRepository;
import com.monopoly.dto.response.LoanPreviewResponse;
import com.monopoly.dto.response.PlayerHistoryResponse;
import com.monopoly.service.LoanService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    private LoanService loanService;

    public Mapper(LoanService loanService) {
        this.loanService = loanService;
    }

    public static void mapToParentHousingOption(HousingOption parents) {
        parents.setHousingType(HousingType.PARENT_HOUSE);
        parents.setName("Stay with Parents/Guardian");
        parents.setBaseCost(15_000_000L);
        parents.setIsFixedRate(false);
        parents.setFixedRatePercent(null);
        parents.setInflationRatePerDicePip(0.02);
    }

    public static void mapToSharedApartmentHousingOption(HousingOption shared) {
        shared.setHousingType(HousingType.SHARED_APARTMENT);
        shared.setName("Shared Apartment");
        shared.setBaseCost(30_000_000L);
        shared.setIsFixedRate(true);
        shared.setFixedRatePercent(0.20);
        shared.setInflationRatePerDicePip(null);
    }

    public static void mapToSingleHousingOption(HousingOption single) {
        single.setHousingType(HousingType.SINGLE_APARTMENT);
        single.setName("Single Apartment");
        single.setBaseCost(90_000_000L);
        single.setIsFixedRate(true);
        single.setFixedRatePercent(0.15);
        single.setInflationRatePerDicePip(null);
    }

    public static void mapToLuxuryApartmentHousingOption(HousingOption luxury) {
        luxury.setHousingType(HousingType.LUXURY_APARTMENT_NINU_LEKKI);
        luxury.setName("Luxury Apartment");
        luxury.setBaseCost(150_000_000L);
        luxury.setIsFixedRate(true);
        luxury.setFixedRatePercent(0.05);
        luxury.setInflationRatePerDicePip(null);
    }

    public static void mapToJobLoss(DiceEvent jobLoss) {
        jobLoss.setDiceValue(1);
        jobLoss.setEventType(DiceEventType.JOB_LOSS);
        jobLoss.setTitle("Job Loss!");
        jobLoss.setDescription("You lost your job. You will not receive your salary next round.");
        jobLoss.setAmountKobo(0L);
        jobLoss.setReturnAmountKobo(null);
        jobLoss.setIsSpreadReturn(false);
    }

    public static void mapToBadInvestment(DiceEvent badInvestment) {
        badInvestment.setDiceValue(6);
        badInvestment.setEventType(DiceEventType.BAD_INVESTMENT);
        badInvestment.setTitle("Risky Investment!");
        badInvestment.setDescription("Invest ₦600,000 now and receive ₦340,000 spread over remaining rounds.");
        badInvestment.setAmountKobo(60_000_000L);
        badInvestment.setReturnAmountKobo(34_000_000L);
        badInvestment.setIsSpreadReturn(true);
    }

    public static void mapToGoodInvestment(DiceEvent goodInvestment) {
        goodInvestment.setDiceValue(5);
        goodInvestment.setEventType(DiceEventType.GOOD_INVESTMENT);
        goodInvestment.setTitle("Investment Opportunity!");
        goodInvestment.setDescription("Invest ₦500,000 now and receive ₦900,000 in the next round.");
        goodInvestment.setAmountKobo(50_000_000L);
        goodInvestment.setReturnAmountKobo(90_000_000L);
        goodInvestment.setIsSpreadReturn(false);
    }

    public static void mapToFamilySupport(DiceEvent support) {
        support.setDiceValue(4);
        support.setEventType(DiceEventType.FAMILY_SUPPORT);
        support.setTitle("Family Support!");
        support.setDescription("Your family supports you with ₦200,000.");
        support.setAmountKobo(20_000_000L);
        support.setReturnAmountKobo(null);
        support.setIsSpreadReturn(false);
    }

    public static void mapToFamily(DiceEvent family) {
        family.setDiceValue(3);
        family.setEventType(DiceEventType.FAMILY_EMERGENCY);
        family.setTitle("Family Emergency!");
        family.setDescription("A family emergency costs you ₦300,000.");
        family.setAmountKobo(30_000_000L);
        family.setReturnAmountKobo(null);
        family.setIsSpreadReturn(false);
    }

    public static void mapToMedical(DiceEvent medical) {
        medical.setDiceValue(2);
        medical.setEventType(DiceEventType.MEDICAL_EMERGENCY);
        medical.setTitle("Medical Emergency!");
        medical.setDescription("A medical emergency costs you ₦400,000.");
        medical.setAmountKobo(40_000_000L);
        medical.setReturnAmountKobo(null);
        medical.setIsSpreadReturn(false);
    }

    public static void mapToInvestmentYield(Player player, Game game, DiceEvent event, Investment investment,
            long cost) {
        investment.setPlayer(player);
        investment.setGame(game);
        investment.setInvestmentType(DiceEventType.BAD_INVESTMENT);
        investment.setInvestedInRound(game.getCurrentRound());
        investment.setAmountInvestedKobo(cost);
        investment.setTotalReturnKobo(event.getReturnAmountKobo());
    }

    public static void mapToGoodInvestment(Player player, Game game, DiceEvent event, Investment investment,
            long cost) {
        investment.setPlayer(player);
        investment.setGame(game);
        investment.setInvestmentType(DiceEventType.GOOD_INVESTMENT);
        investment.setInvestedInRound(game.getCurrentRound());
        investment.setAmountInvestedKobo(cost);
        investment.setTotalReturnKobo(event.getReturnAmountKobo());
    }

    public static PlayerHistoryResponse.@NonNull RoundSummary getRoundSummary(PlayerRound pr,
            PlayerHistoryResponse.RoundSummary summary) {
        summary.setRoundNumber(pr.getRound().getRoundNumber());
        summary.setSalaryReceived(formatNaira(pr.getSalaryReceivedKobo()));
        summary.setHousingType(pr.getHousingType());
        summary.setHousingCost(formatNaira(pr.getHousingCostPaidKobo()));
        summary.setSurvivalCost(formatNaira(pr.getSurvivalCostKobo()));
        summary.setLoanPayment(formatNaira(pr.getLoanPaymentKobo()));
        summary.setLoanBalanceAfter(formatNaira(pr.getLoanBalanceAfterKobo()));
        summary.setDiceRoll(pr.getDiceRoll());
        summary.setEventType(pr.getEventType());
        summary.setEventAmount(formatNaira(pr.getEventAmountKobo()));
        summary.setCashBalanceEnd(formatNaira(pr.getCashBalanceEndKobo()));
        summary.setNetWorth(formatNaira(pr.getNetWorthKobo()));
        return summary;
    }

    public static void mapToPlayerResponse(PlayerHistoryResponse response, Player player) {
        response.setPlayerId(player.getId());
        response.setPlayerName(player.getName());
        response.setStatus(player.getStatus());
        response.setCreditScore(player.getCreditScore());
        response.setCurrentCashBalance(formatNaira(player.getCashBalanceKobo()));
        response.setCurrentLoanBalance(formatNaira(player.getLoanBalanceKobo()));
        long netWorth = player.getFinalNetWorthKobo() != null ? player.getFinalNetWorthKobo()
                : (player.getCashBalanceKobo() - player.getLoanBalanceKobo());
        response.setCurrentNetWorth(formatNaira(netWorth));
    }

    public @NonNull LoanPreviewResponse getLoanPreviewResponse(long proposedPaymentNaira, Player player) {
        long proposedPaymentKobo = proposedPaymentNaira * 100;
        long currentLoan = player.getLoanBalanceKobo();
        long balanceAfterPayment = Math.max(0, currentLoan - proposedPaymentKobo);
        long interest = loanService.previewInterest(currentLoan, proposedPaymentKobo);
        long newBalanceNextRound = balanceAfterPayment + interest;

        LoanPreviewResponse response = new LoanPreviewResponse();
        response.setCurrentLoanBalance(formatNaira(currentLoan));
        response.setProposedPayment(formatNaira(proposedPaymentKobo));
        response.setBalanceAfterPayment(formatNaira(balanceAfterPayment));
        response.setInterestIfNotFullyPaid(formatNaira(interest));
        response.setNewBalanceNextRound(formatNaira(newBalanceNextRound));
        response.setTip(buildTip(balanceAfterPayment, interest, currentLoan));
        return response;
    }

    public static String buildTip(long balanceAfterPayment, long interest, long currentLoan) {
        if (balanceAfterPayment == 0) {
            return "Loan fully paid! No interest next round.";
        }
        if (interest > currentLoan * 0.05) {
            return "High interest next round — consider paying more now.";
        }
        return "Interest will be added to your remaining balance at the start of next round.";
    }

    private static String formatNaira(long kobo) {
        return String.format("₦%,d", kobo / 100);
    }

}
