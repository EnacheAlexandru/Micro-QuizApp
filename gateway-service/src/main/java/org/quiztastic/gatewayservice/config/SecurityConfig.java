package org.quiztastic.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.model.Role;
import org.quiztastic.gatewayservice.model.UserApp;
import org.quiztastic.gatewayservice.service.UserAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

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

    @Bean RefreshUserDetailsService userDetailsService(PasswordEncoder encoder) {
        List<UserApp> userAppList = userAppService.getUserAppList();
        if (userAppList.isEmpty()) {
            try {
                UserApp defaultUserApp = UserApp.builder()
                        .username("default")
                        .password("default")
                        .role(Role.USER)
                        .build();

                userAppService.createUserApp(defaultUserApp.getUsername(), defaultUserApp.getPassword(), defaultUserApp.getRole(), encoder);
                userAppList.add(defaultUserApp);
            } catch (IllegalAccessException e) {
                // ignored
            }
        }

        List<UserDetails> userDetailsList = userAppList.stream()
                .map(userApp -> User.builder()
                        .username(userApp.getUsername())
                        .password(userApp.getPassword())
                        .roles(userApp.getRole().name())
                        .build()
                )
                .toList();

        return new RefreshUserDetailsService(userDetailsList);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthConverter authConverter, AuthManager authManager) {
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authManager);
        jwtFilter.setServerAuthenticationConverter(authConverter);

        return http
                .cors(Customizer.withDefaults())
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
