package com.monopoly.controller;

import com.monopoly.dto.request.PlayRoundRequest;
import com.monopoly.dto.response.ApiResponse;
import com.monopoly.dto.response.LeaderBoard;
import com.monopoly.dto.response.RoundResultResponse;
import com.monopoly.service.RoundService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monopoly/rounds")
@AllArgsConstructor
public class RoundController {
    private final RoundService roundService;

    @PostMapping("/players/{playerId}/play")
    public ResponseEntity<?> playRound(@PathVariable("playerId") Long playerId, @RequestBody PlayRoundRequest request) {
        long loanPayment = request.getLoanPaymentNaira();
        RoundResultResponse result = roundService.playRound(playerId, loanPayment);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                .ok("Round " + result.getRoundNumber() + " completed for " + result.getPlayerName(), result));
    }

    @GetMapping("/{gameCode}/leaderboard/{roundNumber}")
    public ResponseEntity<?> getLeaderboard(@PathVariable("gameCode") String gameCode,
            @PathVariable("roundNumber") int roundNumber) {
        LeaderBoard leaderboard = roundService.getLeaderboard(gameCode, roundNumber);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(leaderboard));
    }
}
