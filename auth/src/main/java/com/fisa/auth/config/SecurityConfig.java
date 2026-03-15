package com.fisa.auth.config;

import com.fisa.auth.authentication.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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

        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage("/oauth2/consent")
                );

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

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
    @Order(2)
    @Bean
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

    // 통합 서버 테스트 시
    @Bean
    @Order(3)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/","/register","/oauth2/consent", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)  // 세션 무효화
                        .clearAuthentication(true)    // 인증 정보 삭제
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }
}