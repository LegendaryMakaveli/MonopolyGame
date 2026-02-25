package com.monopoly.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameEvent {

    private EventType eventType;
    private String gameCode;
    private String message;
    private Object data;

    public enum EventType {
        PLAYER_JOINED,
        GAME_STARTED,
        HOUSING_PICKED,
        ROUND_COMPLETED,
        ALL_PLAYERS_DONE,
        GAME_FINISHED,
        PLAYER_REMOVED
    }

    public static GameEvent of(EventType type, String gameCode, String message, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(type);
        event.setGameCode(gameCode);
        event.setMessage(message);
        event.setData(data);
        return event;
    }
}
