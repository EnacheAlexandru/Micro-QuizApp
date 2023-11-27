package org.quiztastic.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.model.UserApp;
import org.quiztastic.gatewayservice.service.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthManager implements ReactiveAuthenticationManager {

    private final RefreshUserDetailsService userDetailsService;

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(BearerToken.class)
                .flatMap(auth -> {
                    String username;
                    try {
                        username = jwtService.extractUsername(auth.getCredentials(), null);
                    } catch (Exception e) {
                        return Mono.error(new BadCredentialsException("Invalid credentials"));
                    }
                    Mono<UserDetails> foundUser = userDetailsService.findByUsername(username)
                            .defaultIfEmpty(UserApp.builder().build());

                    return foundUser.flatMap(user -> {
                        if (user.getUsername() == null) {
                            return Mono.error(new BadCredentialsException("Invalid credentials"));
                        }
                        if (jwtService.validateJwt(user, auth.getCredentials(), null)) {
                            return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()));
                        }
                        return Mono.error(new BadCredentialsException("Invalid credentials"));
                    });
                });
    }
}
