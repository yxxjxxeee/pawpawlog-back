package com.pawpawlog.global.security;

import com.pawpawlog.auth.dto.request.LoginRequest;
import com.pawpawlog.auth.dto.response.TokenResponse;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.jwt.JwtTokenProvider;
import com.pawpawlog.global.util.HttpResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  public LoginFilter(
      AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider,
      ObjectMapper objectMapper) {
    super(authenticationManager);
    this.jwtTokenProvider = jwtTokenProvider;
    this.objectMapper = objectMapper;
    setFilterProcessesUrl("/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    LoginRequest loginRequest;
    try {
      loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    } catch (IOException e) {
      log.error("로그인 요청 파싱 실패: {}", e.getMessage());
      throw new AuthenticationServiceException("요청 파싱에 실패했습니다.", e);
    }

    if (!StringUtils.hasText(loginRequest.username())
        || !StringUtils.hasText(loginRequest.password())) {
      throw new AuthenticationServiceException("아이디 또는 비밀번호를 입력해주세요.");
    }

    log.debug("로그인 시도: {}", loginRequest.username());

    return getAuthenticationManager().authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.username(), loginRequest.password()));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException {
    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
    log.debug("로그인 성공: {}", userDetails.getUserId());

    TokenResponse tokenResponse = TokenResponse.from(
        jwtTokenProvider.generateToken(authResult));

    HttpResponseUtil.writeSuccessResponse(response, HttpStatus.OK, tokenResponse, objectMapper);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) throws IOException {
    log.warn("로그인 실패: {}", failed.getMessage());
    HttpResponseUtil.writeErrorResponse(response, ErrorCode.UNAUTHORIZED, objectMapper);
  }
}