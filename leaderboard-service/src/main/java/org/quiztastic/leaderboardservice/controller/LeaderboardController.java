package org.quiztastic.leaderboardservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.leaderboardservice.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LeaderboardController {

    private final JwtService jwtService;

    @GetMapping("/ok")
    public ResponseEntity<String> isItOk(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader
    ) {
        String username = getAuthorizedUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            return ResponseEntity.ok(username + " is authenticated on leaderboard");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getAuthorizedUsername(String jwtHeader) {
        if (!jwtService.isJwtHeaderValid(jwtHeader)) {
            return null;
        }

        try {
            return jwtService.extractUsernameHeader(jwtHeader);
        } catch (Exception e) {
            return null;
        }
    }

}
