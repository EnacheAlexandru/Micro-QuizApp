package org.quiztastic.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.dto.AuthResponse;
import org.quiztastic.gatewayservice.dto.LoginRequest;
import org.quiztastic.gatewayservice.model.UserApp;
import org.quiztastic.gatewayservice.service.JwtService;
import org.quiztastic.gatewayservice.service.UserAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GatewayController {

    private final UserAppService userAppService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Optional<UserApp> userApp = userAppService.getUserAppByUsername(request.getUsername());

        if (userApp.isPresent() && request.getPassword() != null && passwordEncoder.matches(request.getPassword(), userApp.get().getPassword())) {
            String generatedJwt = jwtService.generateJwt(request.getUsername());
            return new ResponseEntity<>(
                    AuthResponse.builder()
                            .jwt(generatedJwt)
                            .message("OK")
                            .build(),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                AuthResponse.builder()
                        .jwt("")
                        .message("Invalid credentials")
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @GetMapping("/auth")
    public ResponseEntity<AuthResponse> auth() {
        return new ResponseEntity<>(
                AuthResponse.builder()
                        .jwt("secretToken")
                        .message("Authenticated")
                        .build(),
                HttpStatus.OK
        );
    }
}
