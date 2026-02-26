package com.monopoly.service;

import com.monopoly.data.model.Game;
import com.monopoly.data.model.GameStatus;
import com.monopoly.data.model.Player;
import com.monopoly.data.repository.GameRepository;
import com.monopoly.data.repository.PlayerRepository;
import com.monopoly.exception.GameNotFoundException;
import com.monopoly.exception.InvalidGameActionException;
import com.monopoly.exception.PlayerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameEventPublisher eventPublisher;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<Game> getGamesByStatus(GameStatus status) {
        return gameRepository.findByStatus(status);
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> getPlayersByGame(String gameCode) {
        Game game = gameRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameCode));
        return playerRepository.findByGameIdOrderByTurnOrder(game.getId());
    }

    @Transactional
    public String deletePlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found: " + playerId));

        Game game = player.getGame();

        if (game.getStatus() == GameStatus.IN_PROGRESS)
            throw new InvalidGameActionException("Cannot delete a player while game is in progress. "
                    + "The new player should continue under this player's account.");

        String playerName = player.getName();
        String gameCode = game.getGameCode();

        if (game.getPlayers() != null) {
            game.getPlayers().remove(player);
        }

        playerRepository.delete(player);

        eventPublisher.publishPlayerRemoved(gameCode, playerName);

        return playerName + " has been removed from the game. A new player can now join.";
    }

    @Transactional
    public String deleteAllPlayersInGame(String gameCode) {
        Game game = gameRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameCode));

        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            throw new InvalidGameActionException("Cannot delete players while game is in progress.");
        }

        List<Player> players = playerRepository.findByGameIdOrderByTurnOrder(game.getId());
        game.getPlayers().clear();
        playerRepository.deleteAll(players);

        return "All " + players.size() + " players removed from game " + gameCode + ". Game is ready for new players.";
    }

    @Transactional
    public String deleteGame(String gameCode) {
        Game game = gameRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameCode));

        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            throw new InvalidGameActionException(
                    "Cannot delete a game that is in progress. End the game first.");
        }

        gameRepository.delete(game);
        return "Game " + gameCode + " and all its data have been deleted.";
    }

    @Transactional
    public String forceEndGame(String gameCode) {
        Game game = gameRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameCode));

        if (game.getStatus() == GameStatus.ENDED) {
            throw new InvalidGameActionException("Game is already finished.");
        }

        game.setStatus(GameStatus.ENDED);
        gameRepository.save(game);
        return "Game " + gameCode + " has been force-ended by admin.";
    }
}
