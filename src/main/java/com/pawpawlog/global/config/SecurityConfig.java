package com.pawpawlog.global.config;

import com.pawpawlog.global.jwt.JwtAuthenticationFilter;
import com.pawpawlog.global.jwt.JwtTokenProvider;
import com.pawpawlog.global.redis.RedisDao;
import com.pawpawlog.global.security.CustomAuthenticationEntryPoint;
import com.pawpawlog.global.security.LoginAuthenticationProvider;
import com.pawpawlog.global.security.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  public static final String[] WHITE_LIST = {
      "/auth/login",
      "/auth/reissue",
      "/users/signup",
      "/users/usernames/*/exists",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/health"
  };

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisDao redisDao;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final ObjectMapper objectMapper;
  private final LoginAuthenticationProvider loginAuthenticationProvider;

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(loginAuthenticationProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    AuthenticationManager authenticationManager = authenticationManager();

    LoginFilter loginFilter = new LoginFilter(
        authenticationManager, jwtTokenProvider, objectMapper);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
        jwtTokenProvider, redisDao, objectMapper);

    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers(WHITE_LIST).permitAll()
                .anyRequest().authenticated())
        .exceptionHandling(exception ->
            exception.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}