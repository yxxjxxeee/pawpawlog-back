package com.pawpawlog.auth.service;

import com.pawpawlog.auth.dto.request.ReissueRequest;
import com.pawpawlog.auth.dto.response.TokenResponse;
import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.jwt.JwtTokenProvider;
import com.pawpawlog.global.redis.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final String BEARER_PREFIX = "Bearer ";
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisDao redisDao;

  public TokenResponse reissue(ReissueRequest request) {
    String id = jwtTokenProvider.validateRefreshTokenAndGetId(request.refreshToken());

    redisDao.deleteRefreshToken(id);

    return TokenResponse.from(jwtTokenProvider.reissueToken(id));
  }

  public void logout(String bearerToken) {
    if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    String accessToken = bearerToken.substring(BEARER_PREFIX.length());
    jwtTokenProvider.validateTokenIgnoreExpiry(accessToken);

    String id = jwtTokenProvider.getIdFromToken(accessToken);
    redisDao.deleteRefreshToken(id);
    long remaining = jwtTokenProvider.getRemainingExpiry(accessToken);
    if (remaining > 0) {
      redisDao.saveBlacklist(accessToken, remaining);
    }
  }
}