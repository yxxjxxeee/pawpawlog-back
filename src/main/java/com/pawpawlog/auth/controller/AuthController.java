package com.pawpawlog.auth.controller;

import com.pawpawlog.auth.dto.response.TokenResponse;
import com.pawpawlog.auth.service.AuthService;
import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthService authService;

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 / 리프레시 토큰을 재발급합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "재발급 성공",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = TokenResponse.class))),
      @ApiResponse(responseCode = "401", description = "토큰 없음 / 유효하지 않은 토큰 / 만료된 토큰",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/reissue")
  public ResponseEntity<TokenResponse> reissue(
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    return ResponseEntity.ok(authService.reissue(extractToken(bearerToken)));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "로그아웃", description = "액세스 토큰을 블랙리스트에 등록하고 로그아웃합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "로그아웃 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "토큰 없음 / 유효하지 않은 토큰",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    authService.logout(extractToken(bearerToken));
    return ResponseEntity.noContent().build();
  }

  private String extractToken(String bearerToken) {
    if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
      throw new CustomException(ErrorCode.MISSING_TOKEN);
    }
    return bearerToken.substring(BEARER_PREFIX.length());
  }
}
