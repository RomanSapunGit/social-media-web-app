package com.roman.sapun.java.socialmedia.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Order(2)
    @Bean
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic().and().csrf().disable().authorizeHttpRequests(requests -> requests
                .requestMatchers("/account/**").permitAll());
        return http.build();
    }

    @Order(1)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //TODO find out why .csrf() blocks any request method
        http.httpBasic().and().csrf().disable().securityMatcher("/api/**")
                .authorizeHttpRequests(requests -> requests
                        .anyRequest().hasAnyRole("USER", "ADMIN"))
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint());
        return http.build();
    }

    @Order(3)
    @Bean
    public SecurityFilterChain loginFormFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic().and().authorizeHttpRequests(requests -> requests
                        .anyRequest().authenticated())
                .formLogin(form -> form //TODO adapt it for a frontend
                        .loginPage("/login")
                        .defaultSuccessUrl("/api/user")
                        .permitAll());
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad credentials");
    }
}
