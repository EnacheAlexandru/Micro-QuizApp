package org.quiztastic.leaderboardservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.leaderboardservice.dto.NewRecordDTO;
import org.quiztastic.leaderboardservice.dto.PaginatedLeaderboardResponse;
import org.quiztastic.leaderboardservice.model.Player;
import org.quiztastic.leaderboardservice.repository.LeaderboardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public NewRecordDTO updateLeaderboard(String username, boolean isCorrect) {
        Long highScore = leaderboardRepository.findHighScore();
        if (highScore == null) {
            highScore = 0L;
        }

        Optional<Player> player = leaderboardRepository.findPlayerByUsername(username);

        if (player.isEmpty()) {
            Player newPlayer = Player.builder()
                    .username(username)
                    .points(isCorrect ? 1L : 0L)
                    .total(1L)
                    .build();

            leaderboardRepository.save(newPlayer);

            return NewRecordDTO.builder()
                    .username(newPlayer.getUsername())
                    .points(newPlayer.getPoints())
                    .isNewRecord(newPlayer.getPoints() > highScore)
                    .build();
        }

        player.get().setTotal(player.get().getTotal() + 1);
        if (isCorrect) {
            player.get().setPoints(player.get().getPoints() + 1);
        }

        leaderboardRepository.save(player.get());

        return NewRecordDTO.builder()
                .username(player.get().getUsername())
                .points(player.get().getPoints())
                .isNewRecord(player.get().getPoints() > highScore)
                .build();
    }

}
