package com.fisa.auth.config;

import com.fisa.auth.authentication.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
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
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.UUID;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Order(1)
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

//        authorizationServerConfigurer
//                .authorizationEndpoint(authorizationEndpoint ->
//                        // 본인이 만든 컨트롤러의 GetMapping URL을 입력한다. (예: "/oauth2/consent")
//                        authorizationEndpoint.consentPage("/oauth3/consent")
//                );

        authorizationServerConfigurer
                .authorizationEndpoint(Customizer.withDefaults());

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
//
        http
                .securityMatcher(endpointsMatcher) // OAuth2 관련 엔드포인트만 가로챔
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults()); // OpenID Connect 사용

        http.exceptionHandling(exceptions -> exceptions
                .defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"), // 인증 안된 사용자는 1번 담당자의 로그인 페이지로 보냄
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
        );

        return http.build();
    }

    /**
     * SecurityConfig에서
     * failureUrl("/login?error=true"),
     * logoutSuccessUrl("/login?logout=true")로
     * 설정했기 때문에 URL 파라미터로 에러/로그아웃 상태를 받아서 메시지를 표시해야함.
     */
    // 개발자 전용 로그인 및 콘솔 접근 제어 체인
//    @Order(2)
//    @Bean
//    public SecurityFilterChain developerFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/developer/**", "/console/**")
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/developer/register",
//                                "/developer/login",
//                                "/css/**",
//                                "/js/**"
//                        ).permitAll()
//                        .requestMatchers("/console/**").hasRole("DEVELOPER")
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/developer/login")
//                        .loginProcessingUrl("/developer/login")
//                        .defaultSuccessUrl("/console", true)
//                        .failureUrl("/developer/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/developer/logout")
//                        .logoutSuccessUrl("/developer/login?logout=true")
//                        .permitAll()
//                )
//                .userDetailsService(userDetailsService);
//
//        return http.build();
//    }

    @Bean
    @Order(3)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("http = " + http);
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register","/oauth3/consent", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/oauth3/consent")
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JdbcOAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName("Your client name")
                .clientId("test-client")
                .clientSecret(passwordEncoder().encode("your-secret"))
                .clientAuthenticationMethods(methods -> {
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);

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
