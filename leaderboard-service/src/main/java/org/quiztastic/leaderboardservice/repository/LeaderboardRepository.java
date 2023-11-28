package org.quiztastic.leaderboardservice.repository;

import org.quiztastic.leaderboardservice.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderboardRepository extends JpaRepository<Player, Long> {

}
