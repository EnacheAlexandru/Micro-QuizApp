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

    public PaginatedLeaderboardResponse getLeaderboard(int page) {
        int PAGE_SIZE = 10;
        Page<Player> playerListPaginated = leaderboardRepository
                .findAll(PageRequest.of(page - 1, PAGE_SIZE).withSort(Sort.Direction.DESC, "points"));

        return PaginatedLeaderboardResponse.builder()
                .page(page)
                .pages(playerListPaginated.getTotalPages())
                .players(playerListPaginated.stream().toList())
                .build();
    }

}
