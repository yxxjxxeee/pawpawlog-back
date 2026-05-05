package com.pawpawlog.auth.service;

import com.pawpawlog.auth.dto.response.TokenResponse;
import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.jwt.JwtToken;
import com.pawpawlog.global.jwt.JwtTokenProvider;
import com.pawpawlog.global.redis.RedisDao;
import com.pawpawlog.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  @Value("${jwt.refresh-expiration}")
  private long refreshExpiration;

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisDao redisDao;

  public TokenResponse issueTokenForAuth(Authentication authentication) {
    JwtToken tokenPair = jwtTokenProvider.generateToken(authentication);
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    redisDao.saveRefreshToken(String.valueOf(userDetails.getUserId()), tokenPair.refreshToken(), refreshExpiration);
    return TokenResponse.from(tokenPair);
  }

  public TokenResponse issueTokenForUser(String userId) {
    JwtToken tokenPair = jwtTokenProvider.generateTokenForUserId(userId);
    redisDao.saveRefreshToken(userId, tokenPair.refreshToken(), refreshExpiration);
    return TokenResponse.from(tokenPair);
  }

  public TokenResponse reissue(String refreshToken) {
    String id = jwtTokenProvider.validateRefreshTokenAndGetId(refreshToken);

    String savedToken = redisDao.getRefreshToken(id);
    if (savedToken == null) {
      throw new CustomException(ErrorCode.MISSING_TOKEN);
    }
    if (!refreshToken.equals(savedToken)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    redisDao.deleteRefreshToken(id);
    JwtToken tokenPair = jwtTokenProvider.generateTokenForUserId(id);
    redisDao.saveRefreshToken(id, tokenPair.refreshToken(), refreshExpiration);
    return TokenResponse.from(tokenPair);
  }

  public void logout(String accessToken) {
    jwtTokenProvider.validateTokenIgnoreExpiry(accessToken);

    String id = jwtTokenProvider.getIdFromToken(accessToken);
    redisDao.deleteRefreshToken(id);
    long remaining = jwtTokenProvider.getRemainingExpiry(accessToken);
    if (remaining > 0) {
      redisDao.saveBlacklist(accessToken, remaining);
    }
  }
}
