package org.quiztastic.leaderboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.quiztastic.leaderboardservice.model.Player;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedLeaderboardResponse {

    private Integer page;

    private Integer pages;

    private List<Player> players;

}
