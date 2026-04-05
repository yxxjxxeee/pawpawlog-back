package com.pawpawlog.global.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
@RequiredArgsConstructor
public class RedisDao {

  private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
  private static final String BLACKLIST_PREFIX = "blacklist:";

  private final RedisTemplate<String, Object> redisTemplate;

  public void saveRefreshToken(String id, String tokenValue, long expirationMillis) {
    redisTemplate.opsForValue()
        .set(REFRESH_TOKEN_PREFIX + id, tokenValue, expirationMillis, TimeUnit.MILLISECONDS);
  }

  public String getRefreshToken(String id) {
    Object value = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + id);
    return value != null ? value.toString() : null;
  }

  public void deleteRefreshToken(String id) {
    redisTemplate.delete(REFRESH_TOKEN_PREFIX + id);
  }

  public void saveBlacklist(String accessToken, long expirationMillis) {
    String tokenHash = DigestUtils.md5DigestAsHex(accessToken.getBytes());
    redisTemplate.opsForValue()
        .set(BLACKLIST_PREFIX + tokenHash, "logout", expirationMillis, TimeUnit.MILLISECONDS);
  }

  public boolean isBlacklisted(String accessToken) {
    String tokenHash = DigestUtils.md5DigestAsHex(accessToken.getBytes());
    return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + tokenHash));
  }
}