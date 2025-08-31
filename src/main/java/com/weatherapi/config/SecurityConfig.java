package com.weatherapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${metrics.auth.enabled:true}")
    private boolean authEnabled;

    @Bean
    SecurityFilterChain actuatorChain(HttpSecurity http) throws Exception {
        http.securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(EndpointRequest.to("health")).permitAll();
                    auth.requestMatchers(EndpointRequest.to("info")).permitAll();
                    if (authEnabled) {
                        auth.requestMatchers(EndpointRequest.to("prometheus")).hasRole("PROM_SCRAPER");
                        auth.anyRequest().authenticated();
                    } else {
                        auth.anyRequest().permitAll();
                    }
                })
                .csrf(AbstractHttpConfigurer::disable);

        if (authEnabled) http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService users(PasswordEncoder encoder) {
        String user = System.getenv().getOrDefault("PROM_USER", "prometheus");
        String rawPassword = System.getenv().getOrDefault("PROM_PASSWORD", "secret");

        var details = User.withUsername(user)
                .password(encoder.encode(rawPassword))
                .roles("PROM_SCRAPER")
                .build();
        return new InMemoryUserDetailsManager(details);
    }
}
