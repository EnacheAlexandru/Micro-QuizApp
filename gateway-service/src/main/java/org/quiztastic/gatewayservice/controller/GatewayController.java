package org.quiztastic.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.config.RefreshUserDetailsService;
import org.quiztastic.gatewayservice.dto.GenericResponse;
import org.quiztastic.gatewayservice.dto.LoginRequest;
import org.quiztastic.gatewayservice.model.Role;
import org.quiztastic.gatewayservice.model.UserApp;
import org.quiztastic.gatewayservice.service.JwtService;
import org.quiztastic.gatewayservice.service.UserAppService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GatewayController {

    private final UserAppService userAppService;

    private final JwtService jwtService;

    private final PasswordEncoder encoder;

    private final RefreshUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody LoginRequest request) {
        try {
            if (userAppService.loginUserApp(request.getUsername(), request.getPassword(), encoder)) {
                String generatedJwt = jwtService.generateJwt(request.getUsername(), null);
                return ResponseEntity.ok(GenericResponse.builder().message(generatedJwt).build());
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody LoginRequest request) {
        try {
            UserApp newUserApp = userAppService.createUserApp(request.getUsername(), request.getPassword(), Role.USER, encoder);
            userDetailsService.addUserDetails(request.getUsername(), newUserApp);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/auth")
    public ResponseEntity<GenericResponse> auth(Authentication auth) {
        String authUsername = auth.getName();
        String authSuccessMessage = MessageFormat.format("Hello {0}! You are authenticated", authUsername);
        return ResponseEntity.ok(GenericResponse.builder().message(authSuccessMessage).build());
    }
}
