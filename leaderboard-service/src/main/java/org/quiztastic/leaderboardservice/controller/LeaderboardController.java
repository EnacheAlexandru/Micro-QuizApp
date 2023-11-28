package org.quiztastic.leaderboardservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.leaderboardservice.dto.PaginatedLeaderboardResponse;
import org.quiztastic.leaderboardservice.service.JwtService;
import org.quiztastic.leaderboardservice.service.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LeaderboardController {

    private final JwtService jwtService;

    private final LeaderboardService leaderboardService;

    @GetMapping("/list")
    public ResponseEntity<PaginatedLeaderboardResponse> requestGetLeaderboard(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @RequestParam(required = false) Integer page
    ) {
        String username = getAuthUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            PaginatedLeaderboardResponse playerList = leaderboardService.getLeaderboard(page);
            return ResponseEntity.ok(playerList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getAuthUsername(String jwtBearer) {
        if (!jwtService.isJwtBearerValid(jwtBearer, null)) {
            return null;
        }

        try {
            return jwtService.extractUsernameJwtBearer(jwtBearer, null);
        } catch (Exception e) {
            return null;
        }
    }

}
