//package com.fisa.auth.config;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
//import org.springframework.security.oauth2.core.oidc.OidcScopes;
//import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
//import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
//import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
//
//import java.util.UUID;
//
//@Configuration
//public class DataInitializer {
//
//    @Bean
//    public CommandLineRunner initData(RegisteredClientRepository registeredClientRepository) {
//        return args -> {
//            // DB에 test-client가 없을 때만 인서트
//            if (registeredClientRepository.findByClientId("test-client") == null) {
//                RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
//                        .clientId("test-client")
//                        .clientSecret("{noop}secret")
//                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                        .redirectUri("http://localhost:3000/api/auth/callback/fisa")
//                        .scope(OidcScopes.OPENID)
//                        .scope(OidcScopes.PROFILE)
//                        .scope("email")
//                        .clientSettings(ClientSettings.builder()
//                                .requireAuthorizationConsent(false) // 동의 화면 생략
//                                .requireProofKey(false) // PKCE 생략
//                                .build())
//                        .build();
//
//                registeredClientRepository.save(registeredClient);
//            }
//        };
//    }
//}