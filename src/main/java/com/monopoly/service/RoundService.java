package com.monopoly.service;

import com.monopoly.data.model.*;
import com.monopoly.data.repository.GameRepository;
import com.monopoly.data.repository.PlayerRepository;
import com.monopoly.data.repository.PlayerRoundRepository;
import com.monopoly.data.repository.RoundRepository;
import com.monopoly.dto.response.LeaderBoard;
import com.monopoly.dto.response.RoundResultResponse;
import com.monopoly.exception.InvalidGameActionException;
import com.monopoly.exception.PlayerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoundService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final RoundRepository roundRepository;
    private final PlayerRoundRepository playerRoundRepository;

    private final HousingService housingService;
    private final LoanService loanService;
    private final DiceService diceService;
    private final InvestmentService investmentService;
    private final GameEventPublisher eventPublisher;
    private static final long SURVIVAL_COST_KOBO = 70_000_000L;
    private static final long SALARY_KOBO = 240_000_000L;

    @Transactional
    public RoundResultResponse playRound(Long playerId, long loanPayment) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found: " + playerId));

        Game game = player.getGame();
        validateGameInProgress(game);

        if (player.getHousingType() == null) {
            throw new InvalidGameActionException("Player must pick housing before playing the round.");
        }

        Round currentRound = roundRepository.findByGameIdAndRoundNumber(game.getId(), game.getCurrentRound())
                .orElseThrow(() -> new InvalidGameActionException("Current round not found."));

        boolean alreadyPlayed = playerRoundRepository.findByPlayerIdAndRoundId(playerId, currentRound.getId())
                .isPresent();
        if (alreadyPlayed) {
            throw new InvalidGameActionException("Player has already completed this round.");
        }

        PlayerRound playerRound = new PlayerRound();
        playerRound.setPlayer(player);
        playerRound.setRound(currentRound);
        playerRound.setHousingType(player.getHousingType());
        playerRound.setSurvivalCostKobo(SURVIVAL_COST_KOBO);

        investmentService.payoutPendingInvestments(player, game.getCurrentRound(), game.getTotalRounds());

        long salary = 0L;
        if (!player.getMissNextSalary()) {
            salary = player.getMonthlySalaryKobo();
            player.setCashBalanceKobo(player.getCashBalanceKobo() + salary);
        } else {
            player.setMissNextSalary(false);
            player.setStatus(PlayerStatus.ACTIVE);
        }
        playerRound.setSalaryReceivedKobo(salary);

        int housingDiceRoll = new java.util.Random().nextInt(6) + 1;
        long housingCost = housingService.calculateHousingCost(
                player.getHousingType(), game.getCurrentRound(), housingDiceRoll);
        player.setCashBalanceKobo(player.getCashBalanceKobo() - housingCost);
        playerRound.setHousingCostPaidKobo(housingCost);

        player.setCashBalanceKobo(player.getCashBalanceKobo() - SURVIVAL_COST_KOBO);

        long loanBalanceAfter = loanService.makeLoanPayment(player, loanPayment);
        playerRound.setLoanPaymentKobo(loanPayment);
        playerRound.setLoanBalanceAfterKobo(loanBalanceAfter);

        loanService.applyInterest(player);

        DiceService.DiceRollResult diceResult = diceService.rollAndApplyEvent(player, game);
        playerRound.setDiceRoll(diceResult.diceRoll());
        playerRound.setEventType(diceResult.eventType());
        playerRound.setEventAmountKobo(diceResult.eventAmountKobo());

        long investmentValue = investmentService.calculateTotalInvestmentValue(player);
        long netWorth = player.getCashBalanceKobo() - player.getLoanBalanceKobo() + investmentValue;
        player.setFinalNetWorthKobo(netWorth);
        playerRound.setCashBalanceEndKobo(player.getCashBalanceKobo());
        playerRound.setNetWorthKobo(netWorth);
        playerRound.setIsCompleted(true);
        playerRound.setCompletedAt(LocalDateTime.now());

        playerRepository.save(player);
        playerRoundRepository.save(playerRound);

        RoundResultResponse response = buildRoundResult(playerRound, diceResult.eventDescription());
        eventPublisher.publishRoundCompleted(game.getGameCode(), response);

        checkAndAdvanceRound(game, currentRound);

        return response;
    }

    private void checkAndAdvanceRound(Game game, Round currentRound) {
        int totalPlayers = playerRepository.countByGameId(game.getId());
        int completedPlayers = playerRoundRepository.countByRoundIdAndIsCompleted(currentRound.getId(), true);

        if (completedPlayers < totalPlayers) {
            return;
        }

        currentRound.setIsCompleted(true);
        currentRound.setCompletedAt(LocalDateTime.now());
        roundRepository.save(currentRound);

        boolean isLastRound = game.getCurrentRound() >= game.getTotalRounds();

        if (isLastRound) {
            endGame(game);
        } else {
            int nextRoundNumber = game.getCurrentRound() + 1;
            game.setCurrentRound(nextRoundNumber);
            game.setCurrentPhase(RoundPhase.HOUSING);

            Round nextRound = new Round();
            nextRound.setGame(game);
            nextRound.setRoundNumber(nextRoundNumber);
            roundRepository.save(nextRound);

            gameRepository.save(game);
        }
    }

    private void endGame(Game game) {
        game.setStatus(GameStatus.ENDED);
        game.setEndedAt(LocalDateTime.now());

        List<Player> players = playerRepository.findByGameIdOrderByTurnOrder(game.getId());
        players.forEach(p -> {
            if (p.getStatus() != PlayerStatus.ELIMINATED) {
                p.setStatus(PlayerStatus.FINISHED);
            }
            playerRepository.save(p);
        });

        gameRepository.save(game);
        eventPublisher.publishGameFinished(game.getGameCode(),
                getLeaderboard(game.getGameCode(), game.getCurrentRound()));
    }

    public LeaderBoard getLeaderboard(String gameCode, int roundNumber) {
        Game game = gameRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new InvalidGameActionException("Game not found: " + gameCode));

        Round round = roundRepository.findByGameIdAndRoundNumber(game.getId(), roundNumber)
                .orElseThrow(() -> new InvalidGameActionException("Round " + roundNumber + " not found."));

        List<PlayerRound> roundResults = playerRoundRepository.findRoundLeaderboard(round.getId());

        List<LeaderBoard.PlayerStanding> standings = new ArrayList<>();
        int rank = 1;
        for (PlayerRound pr : roundResults) {
            LeaderBoard.PlayerStanding standing = new LeaderBoard.PlayerStanding();
            standing.setRank(rank++);
            standing.setPlayerId(pr.getPlayer().getId());
            standing.setPlayerName(pr.getPlayer().getName());
            standing.setNetWorthKobo(pr.getNetWorthKobo());
            standing.setCashBalanceKobo(pr.getCashBalanceEndKobo());
            standing.setLoanBalanceKobo(pr.getLoanBalanceAfterKobo());
            standings.add(standing);
        }

        LeaderBoard leaderboard = new LeaderBoard();
        leaderboard.setRoundNumber(roundNumber);
        leaderboard.setStandings(standings);
        return leaderboard;
    }

    private RoundResultResponse buildRoundResult(PlayerRound pr, String eventDescription) {
        RoundResultResponse result = new RoundResultResponse();
        result.setPlayerId(pr.getPlayer().getId());
        result.setPlayerName(pr.getPlayer().getName());
        result.setRoundNumber(pr.getRound().getRoundNumber());
        result.setSalaryReceivedKobo(pr.getSalaryReceivedKobo());
        result.setHousingType(pr.getHousingType());
        result.setHousingCostKobo(pr.getHousingCostPaidKobo());
        result.setSurvivalCostKobo(pr.getSurvivalCostKobo());
        result.setLoanPaymentKobo(pr.getLoanPaymentKobo());
        result.setLoanBalanceRemainingKobo(pr.getLoanBalanceAfterKobo());
        result.setDiceRoll(pr.getDiceRoll());
        result.setEventType(pr.getEventType());
        result.setEventDescription(eventDescription);
        result.setEventAmountKobo(pr.getEventAmountKobo());
        result.setCashBalanceEndKobo(pr.getCashBalanceEndKobo());
        result.setNetWorthKobo(pr.getNetWorthKobo());
        return result;
    }

    private void validateGameInProgress(Game game) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new InvalidGameActionException("Game is not in progress.");
        }
    }
}
