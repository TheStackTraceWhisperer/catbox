package com.example.routebox.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the RouteBox server.
 * 
 * Security is disabled by default and can be enabled via the 'secure' profile.
 * When enabled, uses OAuth2/OIDC authentication with Keycloak.
 * 
 * To enable security:
 * - Set spring.profiles.active=secure
 * - Configure OAuth2 client properties for Keycloak
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security filter chain when security is disabled (default).
     * All requests are permitted without authentication.
     */
    @Bean
    @Profile("!secure")
    public SecurityFilterChain disabledSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }

    /**
     * Security filter chain when security is enabled via 'secure' profile.
     * Uses OAuth2 login with Keycloak for authentication.
     */
    @Bean
    @Profile("secure")
    public SecurityFilterChain enabledSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health/**", "/actuator/prometheus").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/", true)
            );
        
        return http.build();
    }
}
