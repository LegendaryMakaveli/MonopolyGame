package com.monopoly.controller;


import com.monopoly.data.model.Game;
import com.monopoly.data.model.Player;
import com.monopoly.data.repository.PlayerRepository;
import com.monopoly.dto.request.JoinGameRequest;
import com.monopoly.dto.request.PickHousingRequest;
import com.monopoly.dto.response.ApiResponse;
import com.monopoly.dto.response.GameResponse;
import com.monopoly.service.GameService;
import com.monopoly.util.ResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monopoly/games")
@AllArgsConstructor
public class GameController {
    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final ResponseMapper responseMapper;



    @PostMapping("/createGame")
    public ResponseEntity<?> createGame() {
            Game game = gameService.createGame();
            List<Player> players = playerRepository.findByGameIdOrderByTurnOrder(game.getId());
            GameResponse gameResponse = responseMapper.toGameResponse(game, players);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok((gameResponse)));
    }

    @PostMapping("/{gameCode}/join")
    public ResponseEntity<?> joinGame(@PathVariable("gameCode") String gameCode, @RequestBody JoinGameRequest request){
        Player player = gameService.joinGame(gameCode, request.getPlayerName());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(request.getPlayerName() + " joined the game successfully", responseMapper.toPlayerResponse(player)));
    }

    @PostMapping("/{gameCode}/start")
    public ResponseEntity<?> startGame(@PathVariable("gameCode") String gameCode){
        Game game = gameService.startGame(gameCode);
        List<Player> players = playerRepository.findByGameIdOrderByTurnOrder(game.getId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Game started! Round 1 begins.", responseMapper.toGameResponse(game, players)));
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<?> getGame(@PathVariable("gameCode") String gameCode){
        Game game = gameService.getGame(gameCode);
        List<Player> players = playerRepository.findByGameIdOrderByTurnOrder(game.getId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(responseMapper.toGameResponse(game, players)));
    }

    @PostMapping("/players/{playerId}/housing")
    public ResponseEntity<?> pickHousing(@PathVariable("playerId") Long playerId, @RequestBody PickHousingRequest request){
        Player player = gameService.pickHousing(playerId, request.getHousingType());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Housing selected: " + request.getHousingType(), responseMapper.toPlayerResponse(player)));
    }
}
