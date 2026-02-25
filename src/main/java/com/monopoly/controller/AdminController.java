package com.monopoly.controller;


import com.monopoly.data.model.Game;
import com.monopoly.data.model.GameStatus;
import com.monopoly.data.model.Player;
import com.monopoly.dto.response.ApiResponse;
import com.monopoly.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monopoly/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/games")
    public ResponseEntity<?> getAllGames() {
        List<Game> games = adminService.getAllGames();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(games));
    }

    @GetMapping("/games/status/{status}")
    public ResponseEntity<?> getGamesByStatus(@PathVariable("status") GameStatus status) {
        List<Game> games = adminService.getGamesByStatus(status);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(games));
    }

    @GetMapping("/players")
    public ResponseEntity<?> getAllPlayers() {
        List<Player> players = adminService.getAllPlayers();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(players));
    }

    @GetMapping("/games/{gameCode}/players")
    public ResponseEntity<?> getPlayersByGame(@PathVariable("gameCode") String gameCode) {
        List<Player> players = adminService.getPlayersByGame(gameCode);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(players));
    }


    @DeleteMapping("/players/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable("playerId") Long playerId) {
        String message = adminService.deletePlayer(playerId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
    }

    @DeleteMapping("/games/{gameCode}/players")
    public ResponseEntity<?> deleteAllPlayersInGame(@PathVariable("gameCode") String gameCode) {
        String message = adminService.deleteAllPlayersInGame(gameCode);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
    }

    @DeleteMapping("/games/{gameCode}")
    public ResponseEntity<?> deleteGame(@PathVariable("gameCode") String gameCode) {
        String message = adminService.deleteGame(gameCode);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
    }


    @PatchMapping("/games/{gameCode}/force-end")
    public ResponseEntity<?> forceEndGame(@PathVariable("gameCode") String gameCode) {
        String message = adminService.forceEndGame(gameCode);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(message));
    }
}
