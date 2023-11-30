package org.quiztastic.leaderboardservice.config;

import lombok.RequiredArgsConstructor;
import org.quiztastic.leaderboardservice.dto.NewRecordQueueDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.name}")
    private String routingName;

    private final RabbitTemplate rabbitTemplate;

    public void sendNewRecord(NewRecordQueueDTO payload) {
        rabbitTemplate.convertAndSend(exchangeName, routingName, payload);
    }
}
