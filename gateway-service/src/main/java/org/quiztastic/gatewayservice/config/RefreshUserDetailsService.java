package org.quiztastic.gatewayservice.config;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RefreshUserDetailsService implements ReactiveUserDetailsService {

    private final Map<String, UserDetails> userMap;

    public RefreshUserDetailsService(Collection<UserDetails> users) {
        userMap = new ConcurrentHashMap<>();

        for (UserDetails user : users) {
            userMap.put(user.getUsername(), user);
        }
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.justOrEmpty(userMap.get(username))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)));
    }

    public void addUserDetails(String username, UserDetails userDetails) {
        userMap.put(username, userDetails);
    }
}
