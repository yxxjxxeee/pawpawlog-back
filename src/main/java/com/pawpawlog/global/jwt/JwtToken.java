package com.pawpawlog.global.jwt;

public record JwtToken(String grantType, String accessToken, String refreshToken) {

  private static final String GRANT_TYPE = "Bearer";

  public static JwtToken of(String accessToken, String refreshToken) {
    return new JwtToken(GRANT_TYPE, accessToken, refreshToken);
  }
}
