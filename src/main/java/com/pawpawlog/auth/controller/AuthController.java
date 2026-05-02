package com.pawpawlog.auth.controller;

import com.pawpawlog.auth.dto.response.TokenResponse;
import com.pawpawlog.auth.service.AuthService;
import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthService authService;

  @PostMapping("/reissue")
  public ResponseEntity<ApiResponse<TokenResponse>> reissue(
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    TokenResponse tokenResponse = authService.reissue(extractToken(bearerToken));
    return ResponseEntity.ok(ApiResponse.success(tokenResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    authService.logout(extractToken(bearerToken));
    return ResponseEntity.ok(ApiResponse.success());
  }

  private String extractToken(String bearerToken) {
    if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
      throw new CustomException(ErrorCode.MISSING_TOKEN);
    }
    return bearerToken.substring(BEARER_PREFIX.length());
  }
}
