package org.quiztastic.leaderboardservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.leaderboardservice.dto.PaginatedLeaderboardResponse;
import org.quiztastic.leaderboardservice.model.KafkaOperation;
import org.quiztastic.leaderboardservice.service.JwtService;
import org.quiztastic.leaderboardservice.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Map;

@RestController
@RequestMapping(value = "/leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LeaderboardController {

    Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    private final JwtService jwtService;

    private final LeaderboardService leaderboardService;

    @GetMapping("/list")
    public ResponseEntity<PaginatedLeaderboardResponse> requestGetLeaderboard(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer,
            @RequestParam(value = "page", required = false) String pageString
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int page = 1;
        try {
            page = Integer.parseInt(pageString);
            page = Math.max(page, 1);
        } catch (Exception e) {
            // ignore, default to first page
        }

        try {
            PaginatedLeaderboardResponse playerList = leaderboardService.getLeaderboard(page);
            return ResponseEntity.ok(playerList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @KafkaListener(topics = "notificationTopic")
    public void handleKafkaEvent(Map<String, String> payload) {
        logger.info(MessageFormat.format("Received operation {0} with payload {1}", KafkaOperation.UPDATE_LEADERBOARD.name(), payload));

        if (payload.get("operation").equals(KafkaOperation.UPDATE_LEADERBOARD.name())) {
            String username = getAuthUsername(payload.get("token"));
            if (username == null) {
                return;
            }

            leaderboardService.updateLeaderboard(username, Boolean.parseBoolean(payload.get("isCorrect")));
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
