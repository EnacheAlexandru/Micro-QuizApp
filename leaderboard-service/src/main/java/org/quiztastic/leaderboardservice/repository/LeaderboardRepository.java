package org.quiztastic.leaderboardservice.repository;

import org.quiztastic.leaderboardservice.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<Player, Long> {

    Optional<Player> findPlayerByUsername(String username);

}
