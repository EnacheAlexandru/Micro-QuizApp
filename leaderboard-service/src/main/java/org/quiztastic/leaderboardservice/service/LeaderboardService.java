package org.quiztastic.leaderboardservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.leaderboardservice.dto.PaginatedLeaderboardResponse;
import org.quiztastic.leaderboardservice.model.Player;
import org.quiztastic.leaderboardservice.repository.LeaderboardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    public PaginatedLeaderboardResponse getLeaderboard(Integer page) {
        Page<Player> playerListPaginated = leaderboardRepository
                .findAll(PageRequest.of(page == null ? 0 : page - 1, 3).withSort(Sort.Direction.DESC, "points"));

        return PaginatedLeaderboardResponse.builder()
                .pages(playerListPaginated.getTotalPages())
                .players(playerListPaginated.stream().toList())
                .build();
    }

}
