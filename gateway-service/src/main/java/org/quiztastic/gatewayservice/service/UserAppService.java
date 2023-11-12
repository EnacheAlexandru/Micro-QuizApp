package org.quiztastic.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.model.Role;
import org.quiztastic.gatewayservice.model.UserApp;
import org.quiztastic.gatewayservice.repository.UserAppRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserAppRepository userAppRepository;

    public Optional<UserApp> getUserAppByUsername(String username) {
        return userAppRepository.findUserAppByUsername(username);
    }

    public List<UserApp> getUserAppList() {
        return userAppRepository.findAll();
    }

    public void createUserApp(String username, String password, Role role) throws IllegalAccessException {
        if (userAppRepository.existsUserAppByUsername(username)) {
            throw new IllegalAccessException("Username is taken.");
        }

        UserApp userApp = UserApp.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        userAppRepository.save(userApp);
    }
}
