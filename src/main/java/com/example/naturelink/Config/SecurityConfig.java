package com.example.naturelink.Config;

import com.example.naturelink.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    // ✅ Use injected value instead of hardcoded URL
    @Value("${frontend.url}")
    private String frontendUrl;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withCors -> withCors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/picloud/uploads/**").permitAll()
                        .requestMatchers("/picloud/reservations/**").permitAll()
                        .requestMatchers("/picloud/static/**").permitAll()
                        .requestMatchers("/picloud/packs/**").permitAll()
                        .requestMatchers("/picloud/activities/**").permitAll()
                        .requestMatchers("/picloud/transport/**").permitAll()
                        .requestMatchers("/picloud/logements/**").permitAll()
                        .requestMatchers("/picloud/ratings/**").permitAll()
                        .requestMatchers("/picloud/restaurants/**").permitAll()
                        .requestMatchers("/picloud/api/foods/**").permitAll()
                        .requestMatchers("/picloud/api/clothing/**").permitAll()
                        .requestMatchers("/picloud/api/comments/**").permitAll()
                        .requestMatchers("/picloud/api/likes/**").permitAll()
                        .requestMatchers("/picloud/api/destinations").permitAll()
                        .requestMatchers("/picloud/api/footprints/**").permitAll()
                        .requestMatchers("/picloud/auth/**").permitAll()

                        .requestMatchers("/api/visits/**").permitAll()
                        .requestMatchers("/api/monuments/**").permitAll()
                        .requestMatchers("/api/tourguides").permitAll()
                        .requestMatchers("/api/menus/**").permitAll()
                        .requestMatchers("/event/**").permitAll()
                        .requestMatchers("/api/boutiques/**").permitAll()
                        .requestMatchers("/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/favorites/add/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/users/approve").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/users/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/{id}/upload-profile-pic").authenticated()
                        // Payment endpoints - choose one of the following options:
                        // Option 1: All payment endpoints require authentication
                        //.requestMatchers("/api/payments/**").authenticated()
                        // Option 2: All payment endpoints are public
                        .requestMatchers("/api/payments/**").permitAll()
                        .anyRequest().authenticated() // ⛔ À la fin !
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false); // needed for JWT/cookies
        config.setAllowedOriginPatterns(List.of("*")); // wildcard accepted as pattern
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}