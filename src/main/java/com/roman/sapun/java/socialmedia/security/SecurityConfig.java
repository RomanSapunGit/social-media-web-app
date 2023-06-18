package com.roman.sapun.java.socialmedia.security;

import com.roman.sapun.java.socialmedia.filter.JwtAuthFilter;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter authFilter;
    private final UserRepository userRepository;

    @Autowired
    public SecurityConfig(JwtAuthFilter authFilter, UserRepository userRepository) {
        this.authFilter = authFilter;
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

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
        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .securityMatcher("/api/**")
                .authorizeHttpRequests(requests -> requests
                        .anyRequest().hasAnyRole("USER", "ADMIN"))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
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
