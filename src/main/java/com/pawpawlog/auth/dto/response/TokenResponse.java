package com.pawpawlog.auth.dto.response;

import com.pawpawlog.global.jwt.JwtToken;

public record TokenResponse(String grantType, String accessToken, String refreshToken) {

  public static TokenResponse from(JwtToken jwtToken) {
    return new TokenResponse(jwtToken.grantType(), jwtToken.accessToken(),
        jwtToken.refreshToken());
  }
}