package org.quiztastic.gatewayservice.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse extends AbstractResponse {

    private String jwt;
}
