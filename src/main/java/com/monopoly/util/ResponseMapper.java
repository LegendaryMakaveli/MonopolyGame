package com.monopoly.util;

import com.monopoly.data.model.Game;
import com.monopoly.data.model.Player;
import com.monopoly.dto.response.GameResponse;
import com.monopoly.dto.response.PlayerResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseMapper {
    public GameResponse toGameResponse(Game game, List<Player> players) {
        GameResponse response = new GameResponse();
        response.setId(game.getId());
        response.setGameCode(game.getGameCode());
        response.setStatus(game.getStatus());
        response.setCurrentRound(game.getCurrentRound());
        response.setTotalRounds(game.getTotalRounds());
        response.setCurrentPhase(game.getCurrentPhase());
        response.setMaxPlayers(game.getMaxPlayers());
        response.setCurrentPlayerCount(players.size());
        response.setPlayers(players.stream().map(this::toPlayerResponse).toList());
        return response;
    }

    public PlayerResponse toPlayerResponse(Player player) {
        PlayerResponse response = new PlayerResponse();
        response.setId(player.getId());
        response.setName(player.getName());
        response.setStatus(player.getStatus());
        response.setHousingType(player.getHousingType());
        response.setTurnOrder(player.getTurnOrder());
        response.setCreditScore(player.getCreditScore());
        response.setCashBalance(formatNaira(player.getCashBalance()));
        response.setLoanBalance(formatNaira(player.getLoanBalance()));
        response.setNetWorth(formatNaira(player.getCashBalance() - player.getLoanBalance()));
        return response;
    }

    private String formatNaira(long amount) {
        return String.format("₦%,d", amount);
    }
}
