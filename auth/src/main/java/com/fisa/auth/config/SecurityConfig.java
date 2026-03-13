package com.fisa.auth.config;

import com.fisa.auth.authentication.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * SecurityConfig에서
     * failureUrl("/login?error=true"),
     * logoutSuccessUrl("/login?logout=true")로
     * 설정했기 때문에 URL 파라미터로 에러/로그아웃 상태를 받아서 메시지를 표시해야함.
     */
    // 개발자 전용 로그인 및 콘솔 접근 제어 체인
    @Bean
    @Order(1)
    public SecurityFilterChain developerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/developer/**", "/console/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/developer/register",
                                "/developer/login",
                                "/css/**",
                                "/js/**"
                        ).permitAll()
                        .requestMatchers("/console/**").hasRole("DEVELOPER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/developer/login")
                        .loginProcessingUrl("/developer/login")
                        .defaultSuccessUrl("/console", true)
                        .failureUrl("/developer/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/developer/logout")
                        .logoutSuccessUrl("/developer/login?logout=true")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register","/oauth2/consent", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();

    }


    /**
     * CustomUserDetailsService 로 대체
     */
//    //    @Bean
//    public UserDetailsService userDetailsService() {
//        return null;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcOAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName("Your client name")
                .clientId("your-client")
                .clientSecret(passwordEncoder().encode("your-secret"))
                .clientAuthenticationMethods(methods -> {
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                })
                .authorizationGrantTypes(types -> {
                    types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    types.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .redirectUris(uri -> {
                    uri.add("http://localhost:3000");
                })
                .postLogoutRedirectUris(uri -> {
                    uri.add("http://localhost:3000");
                })
                .scopes(scope -> {
                    scope.add(OidcScopes.OPENID);
                    scope.add(OidcScopes.PROFILE);
                })
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcOperations);
        try {
            registeredClientRepository.save(registeredClient);
        } catch (IllegalArgumentException e) {
            log.warn("이미 존재하는 클라이언트 : {}", e.getMessage());
        }

        return registeredClientRepository;
    }


}
