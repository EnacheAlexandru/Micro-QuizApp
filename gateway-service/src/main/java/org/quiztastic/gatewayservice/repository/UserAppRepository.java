package org.quiztastic.gatewayservice.repository;

import org.quiztastic.gatewayservice.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAppRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findUserAppByUsername(String username);

    boolean existsUserAppByUsername(String username);
}
