package org.quiztastic.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.notificationservice.dto.NewRecordWsResponse;
import org.quiztastic.notificationservice.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping(value = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NotificationController {

    Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final JwtService jwtService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/record")
    @SendTo("/topic/records")
    public ResponseEntity<NewRecordWsResponse> wsNewRecord() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    public void fireWsNewRecord(String username, Long points) {
        logger.info(MessageFormat.format("User {0} set a new high score of {1} points", username, points));
        simpMessagingTemplate.convertAndSend(
                "/topic/records",
                NewRecordWsResponse.builder().username(username).points(points).build()
        );
    }

//    @GetMapping("/auth")
//    public ResponseEntity<String> auth(
//            @RequestHeader(value = "Authorization", required = false) String jwtBearer
//    ) {
//        String authUsername = getAuthUsername(jwtBearer);
//        if (authUsername == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        fireWsNewRecord("Leclerc", 206L);
//
//        return ResponseEntity.ok(MessageFormat.format("Hello {0}! You are authenticated", authUsername));
//    }

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
