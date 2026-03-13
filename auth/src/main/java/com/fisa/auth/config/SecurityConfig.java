package com.fisa.auth.config;

import com.fisa.auth.authentication.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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


}
