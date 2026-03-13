package com.fisa.auth.dashboard.repository;

import com.fisa.auth.dashboard.model.OAuth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2RegisteredClientRepository extends JpaRepository<OAuth2RegisteredClient, String> {
    Optional<OAuth2RegisteredClient> findByClientId(String clientId);
}
