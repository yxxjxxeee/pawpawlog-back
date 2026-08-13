package com.pawpawlog.auth.oauth2.handler;

import com.pawpawlog.auth.oauth2.CustomOAuth2User;
import com.pawpawlog.global.redis.RedisDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Value("${app.oauth2.redirect-uri}")
  private String redirectUri;

  @Value("${app.oauth2.code-expiration}")
  private long codeExpirationMillis;

  private final RedisDao redisDao;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    String userId = String.valueOf(oAuth2User.getUserId());

    String code = UUID.randomUUID().toString();
    redisDao.saveOAuth2Code(code, userId, codeExpirationMillis);

    String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
        .queryParam("code", code)
        .build().toUriString();

    log.debug("OAuth2 로그인 성공: userId={}", userId);

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}
