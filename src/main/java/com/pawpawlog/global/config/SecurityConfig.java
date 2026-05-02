package com.pawpawlog.global.config;

import com.pawpawlog.auth.oauth2.CustomOAuth2UserService;
import com.pawpawlog.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import com.pawpawlog.auth.service.AuthService;
import com.pawpawlog.global.jwt.JwtAuthenticationFilter;
import com.pawpawlog.global.jwt.JwtTokenProvider;
import com.pawpawlog.global.redis.RedisDao;
import com.pawpawlog.global.security.CustomAccessDeniedHandler;
import com.pawpawlog.global.security.CustomAuthenticationEntryPoint;
import com.pawpawlog.global.security.LoginAuthenticationProvider;
import com.pawpawlog.global.security.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
      "/auth/login", "/auth/reissue", "/auth/logout",
      "/users", "/users/usernames/*", "/health",
      "/swagger-ui/**", "/v3/api-docs/**",
      "/oauth2/**", "/login/oauth2/**"
  };

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisDao redisDao;
  private final AuthService authService;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final ObjectMapper objectMapper;
  private final LoginAuthenticationProvider loginAuthenticationProvider;
  @Value("${app.oauth2.redirect-uri}")
  private String oauth2RedirectUri;

  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final CustomAccessDeniedHandler accessDeniedHandler;

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(loginAuthenticationProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    AuthenticationManager authenticationManager = authenticationManager();

    LoginFilter loginFilter = new LoginFilter(
        authenticationManager, authService, objectMapper);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
        jwtTokenProvider, redisDao, objectMapper);

    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/reissue", "/auth/logout", "/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/usernames/*", "/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                .anyRequest().authenticated())
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService))
            .successHandler(oAuth2LoginSuccessHandler)
            .failureUrl(oauth2RedirectUri + "?error=oauth2_failed"))
        .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
