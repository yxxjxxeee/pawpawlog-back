package com.pawpawlog.auth.dto.response;

import com.pawpawlog.global.jwt.JwtToken;
import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
    @Schema(description = "액세스 토큰") String accessToken,
    @Schema(description = "리프레시 토큰") String refreshToken
) {

  public static TokenResponse from(JwtToken jwtToken) {
    return new TokenResponse(jwtToken.accessToken(), jwtToken.refreshToken());
  }
}