package org.quiztastic.gatewayservice.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractResponse {

    private String message;
}
