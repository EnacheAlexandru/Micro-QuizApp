package org.quiztastic.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.model.Role;
import org.quiztastic.gatewayservice.model.UserApp;
import org.quiztastic.gatewayservice.repository.UserAppRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserAppRepository userAppRepository;

    Logger logger = LoggerFactory.getLogger(UserAppService.class);

    public Optional<UserApp> getUserAppByUsername(String username) {
        return userAppRepository.findUserAppByUsername(username);
    }

    public List<UserApp> getUserAppList() {
        return userAppRepository.findAll();
    }

    public UserApp createUserApp(String username, String password, Role role, PasswordEncoder encoder) throws IllegalAccessException {
        if (username == null || username.length() < 5 || username.length() > 20) {
            logger.error(MessageFormat.format("Invalid username. Received: {0}", username));
            throw new IllegalAccessException();
        }

        if (password == null || password.length() < 5 || password.length() > 20) {
            logger.error("Invalid password");
            throw new IllegalAccessException();
        }

        if (userAppRepository.existsUserAppByUsername(username)) {
            logger.error(MessageFormat.format("Username {0} is taken", username));
            throw new IllegalAccessException();
        }

        UserApp userApp = UserApp.builder()
                .username(username)
                .password(encoder.encode(password))
                .role(role)
                .build();

        try {
            userAppRepository.save(userApp);
            logger.info(MessageFormat.format("User {0} saved successfully", username));
            return userApp;
        } catch (Exception e) {
            logger.error(MessageFormat.format("Error saving user {0}", username));
            throw new IllegalAccessException();
        }
    }

    public boolean loginUserApp(String username, String password, PasswordEncoder encoder) {
        if (username == null) {
            logger.error("Invalid username. Received: null");
            return false;
        }

        if (password == null) {
            logger.error("Invalid password");
            return false;
        }

        Optional<UserApp> userApp = getUserAppByUsername(username);

        if (userApp.isEmpty()) {
            logger.error(MessageFormat.format("User with username {0} not found", username));
            return false;
        }

        if (!encoder.matches(password, userApp.get().getPassword())) {
            logger.error(MessageFormat.format("Invalid credentials for username {0}", username));
            return false;
        }

        return true;
    }
}
