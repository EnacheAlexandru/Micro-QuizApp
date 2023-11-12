package org.quiztastic.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.model.Role;
import org.quiztastic.gatewayservice.model.UserApp;
import org.quiztastic.gatewayservice.service.UserAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserAppService userAppService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        List<UserApp> userAppList = userAppService.getUserAppList();
        if (userAppList.isEmpty()) {
            try {
                UserApp defaultUserApp = UserApp.builder()
                        .username("default")
                        .password(passwordEncoder.encode("default"))
                        .role(Role.USER)
                        .build();

                userAppService.createUserApp(defaultUserApp.getUsername(), defaultUserApp.getPassword(), defaultUserApp.getRole());
                userAppList.add(defaultUserApp);
            } catch (IllegalAccessException e) {
                // ignored
            }
        }

        List<UserDetails> userDetailsList = userAppList.stream()
                .map(userApp -> User.builder()
                        .username(userApp.getUsername())
                        .password(passwordEncoder.encode(userApp.getPassword()))
                        .roles(userApp.getRole().name())
                        .build()
                )
                .toList();

        return new MapReactiveUserDetailsService(userDetailsList);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthConverter authConverter, AuthManager authManager) {
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authManager);
        jwtFilter.setServerAuthenticationConverter(authConverter);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(auth -> {
                    auth.pathMatchers("/login", "/register").permitAll();
                    auth.anyExchange().authenticated();
                })
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
