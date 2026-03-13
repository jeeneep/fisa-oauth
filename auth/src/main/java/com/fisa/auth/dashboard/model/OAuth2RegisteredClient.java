package com.fisa.auth.dashboard.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Entity
@Table(name = "oauth2_registered_client")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2RegisteredClient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 100, nullable = false, updatable = false)
    private String id;

    @Column(name = "client_id", length = 100, nullable = false, unique = true)
    private String clientId;

    @Column(name = "client_id_issued_at")
    private LocalDateTime clientIdIssuedAt;

    @Column(name = "client_secret", length = 200)
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private LocalDateTime clientSecretExpiresAt;

    @Column(name = "client_name", length = 200, nullable = false)
    private String clientName;

    @Column(name = "client_authentication_methods", length = 1000, nullable = false)
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types", length = 1000, nullable = false)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uris", length = 1000)
    private String redirectUris;

    @Column(name = "post_logout_redirect_uris", length = 1000)
    private String postLogoutRedirectUris;

    @Column(name = "scopes", length = 1000, nullable = false)
    private String scopes;

    @Column(name = "client_settings", length = 2000, nullable = false)
    private String clientSettings;

    @Column(name = "token_settings", length = 2000, nullable = false)
    private String tokenSettings;


    @PrePersist
    public void prePersist() {
        if (this.clientIdIssuedAt == null) {
            this.clientIdIssuedAt = LocalDateTime.now();
        }
    }

    public void updateClient(
            String clientName,
            String clientAuthenticationMethods,
            String authorizationGrantTypes,
            String redirectUris,
            String postLogoutRedirectUris,
            String scopes,
            String clientSettings,
            String tokenSettings
    ) {
        this.clientName = clientName;
        this.clientAuthenticationMethods = clientAuthenticationMethods;
        this.authorizationGrantTypes = authorizationGrantTypes;
        this.redirectUris = redirectUris;
        this.postLogoutRedirectUris = postLogoutRedirectUris;
        this.scopes = scopes;
        this.clientSettings = clientSettings;
        this.tokenSettings = tokenSettings;
    }

    public void changeClientSecret(String clientSecret, LocalDateTime clientSecretExpiresAt) {
        this.clientSecret = clientSecret;
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }
}
