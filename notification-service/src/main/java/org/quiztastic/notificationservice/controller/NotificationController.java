package org.quiztastic.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.notificationservice.dto.NewRecordQueueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/record")
    @SendTo("/topic/records")
    public NewRecordQueueDTO newRecordWebSocket() {
        return NewRecordQueueDTO.builder().username(null).points(null).build();
    }

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void fireNewRecordWebSocket(NewRecordQueueDTO payload) {
        logger.info(MessageFormat.format("User {0} set a new high score of {1} points", payload.getUsername(), payload.getPoints()));
        simpMessagingTemplate.convertAndSend("/topic/records", payload);
    }

}
