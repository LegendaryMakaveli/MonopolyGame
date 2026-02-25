package com.monopoly.service;


import com.monopoly.data.model.GameEvent;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GameEventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    private String getGameTopic(String gameCode) {
        return "/topic/game/" + gameCode;
    }

    public void publishPlayerJoined(String gameCode, Object playerData) {
        messagingTemplate.convertAndSend(getGameTopic(gameCode), GameEvent.of(GameEvent.EventType.PLAYER_JOINED, gameCode, "A new player has joined the game.", playerData));
    }

    public void publishGameStarted(String gameCode, Object gameData) {
        messagingTemplate.convertAndSend(getGameTopic(gameCode), GameEvent.of(GameEvent.EventType.GAME_STARTED, gameCode, "Game has started! Round 1 begins. Pick your housing.", gameData));
    }

    public void publishHousingPicked(String gameCode, Object playerData) {
        messagingTemplate.convertAndSend(getGameTopic(gameCode), GameEvent.of(GameEvent.EventType.HOUSING_PICKED, gameCode, "A player has picked their housing.", playerData));
    }

    public void publishRoundCompleted(String gameCode, Object roundResult) {
        messagingTemplate.convertAndSend(getGameTopic(gameCode), GameEvent.of(GameEvent.EventType.ROUND_COMPLETED, gameCode, "A player has completed their round.", roundResult));
    }

    public void publishAllPlayersDone(String gameCode, Object leaderboard) {
        messagingTemplate.convertAndSend(getGameTopic(gameCode), GameEvent.of(GameEvent.EventType.ALL_PLAYERS_DONE, gameCode, "All players have completed this round. Here are the standings.", leaderboard));
    }

    public void publishGameFinished(String gameCode, Object finalLeaderboard) {
        messagingTemplate.convertAndSend(getGameTopic(gameCode), GameEvent.of(GameEvent.EventType.GAME_FINISHED, gameCode, "Game over! Final standings are in.", finalLeaderboard));
    }

    public void publishPlayerRemoved(String gameCode, String playerName) {
        messagingTemplate.convertAndSend(getGameTopic(gameCode), GameEvent.of(GameEvent.EventType.PLAYER_REMOVED, gameCode, playerName + " has been removed from the game.", null));
    }
}
