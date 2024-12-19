package com.example.checkout.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /*
                 Cors and csrf are turned off
                 */
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
        /*
        Example for authenticated user implementation
        **/
        //.requestMatchers(HttpMethod.GET, "/cart").authenticated()
        //.requestMatchers(HttpMethod.POST, "/checkout/scanItem").hasRole("USER")
        //.requestMatchers(HttpMethod.POST, "/checkout/total").hasRole("USER")
            .requestMatchers("/api/checkout/cart").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/checkout/scan").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/checkout/total").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/checkout/pay").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/checkout/reset").permitAll()
            .anyRequest().denyAll());
        return http.build();
    }

}
