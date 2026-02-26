package com.monopoly.data.repository;

import com.monopoly.data.model.PlayerRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerRoundRepository extends JpaRepository<PlayerRound, Long> {
    List<PlayerRound> findByPlayerIdOrderByRoundRoundNumber(Long playerId);

    Optional<PlayerRound> findByPlayerIdAndRoundId(Long playerId, Long roundId);

    List<PlayerRound> findByRoundId(Long roundId);

    @Query("SELECT pr FROM PlayerRound pr WHERE pr.round.id = :roundId AND pr.isCompleted = true ORDER BY pr.netWorth DESC")
    List<PlayerRound> findRoundLeaderboard(@Param("roundId") Long roundId);

    int countByRoundIdAndIsCompleted(Long roundId, Boolean isCompleted);
}
