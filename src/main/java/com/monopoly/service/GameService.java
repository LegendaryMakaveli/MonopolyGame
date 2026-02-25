package com.monopoly.service;

import com.monopoly.data.model.*;
import com.monopoly.data.repository.*;
import com.monopoly.exception.GameNotFoundException;
import com.monopoly.exception.InvalidGameActionException;
import com.monopoly.exception.PlayerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@AllArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final RoundRepository roundRepository;
    private final PlayerRoundRepository playerRoundRepository;
    private final InvestmentRepository investmentRepository;


    @Transactional
    public void deleteAllData() {
        investmentRepository.deleteAll();
        playerRoundRepository.deleteAll();
        roundRepository.deleteAll();
        playerRepository.deleteAll();
        gameRepository.deleteAll();
    }


    @Transactional
    public Game createGame() {
        Game game = new Game();
        game.setGameCode(generateUniqueGameCode());
        game.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        return gameRepository.save(game);
    }


    @Transactional
    public Player joinGame(String gameCode, String playerName) {
        Game game = findGameByCode(gameCode);

        if (game.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new InvalidGameActionException("Game has already started. Cannot join.");
        }

        int currentPlayerCount = playerRepository.countByGameId(game.getId());
        if (currentPlayerCount >= game.getMaxPlayers()) {
            throw new InvalidGameActionException("Game is full. Maximum " + game.getMaxPlayers() + " players allowed.");
        }

        if (playerRepository.existsByGameIdAndName(game.getId(), playerName)) {
            throw new InvalidGameActionException("A player with name '" + playerName + "' already exists in this game.");
        }

        Player player = new Player();
        player.setName(playerName);
        player.setGame(game);
        player.setTurnOrder(currentPlayerCount + 1);

        return playerRepository.save(player);
    }

    @Transactional
    public Game startGame(String gameCode) {
        Game game = findGameByCode(gameCode);

        if (game.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new InvalidGameActionException("Game has already started.");
        }

        int playerCount = playerRepository.countByGameId(game.getId());
        if (playerCount < 2) {
            throw new InvalidGameActionException("Need at least 2 players to start.");
        }

        game.setStatus(GameStatus.IN_PROGRESS);
        game.setStartedAt(LocalDateTime.now());
        game.setCurrentRound(1);

        Round round1 = createRound(game, 1);
        roundRepository.save(round1);

        return gameRepository.save(game);
    }


    @Transactional
    public Player pickHousing(Long playerId, HousingType housingType) {
        Player player = findPlayer(playerId);
        Game game = player.getGame();

        validateGameInProgress(game);
        validateRoundPhase(game, RoundPhase.HOUSING);

        if (game.getCurrentRound() > 1 && player.getHousingType() != null) {
            throw new InvalidGameActionException("Housing choice is locked after round 1.");
        }

        player.setHousingType(housingType);
        return playerRepository.save(player);
    }

    public Game getGame(String gameCode) {
        return findGameByCode(gameCode);
    }

    private Round createRound(Game game, int roundNumber) {
        Round round = new Round();
        round.setGame(game);
        round.setRoundNumber(roundNumber);
        round.setPhase(RoundPhase.HOUSING);
        return round;
    }

    public Game findGameByCode(String gameCode) {
        return gameRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new GameNotFoundException("Game not found with code: " + gameCode));
    }

    public Player findPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found: " + playerId));
    }

    private void validateGameInProgress(Game game) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new InvalidGameActionException("Game is not in progress.");
        }
    }

    private void validateRoundPhase(Game game, RoundPhase expectedPhase) {
        if (game.getCurrentPhase() != expectedPhase) {
            throw new InvalidGameActionException(
                    "Wrong phase. Expected: " + expectedPhase + ", Current: " + game.getCurrentPhase()
            );
        }
    }

    private String generateUniqueGameCode() {
        String code;
        do {
            code = "IkkaMakaveli-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } while (gameRepository.existsByGameCode(code));
        return code;
    }
}
