package com.pawpawlog.global.jwt;

import com.pawpawlog.global.config.SecurityConfig;
import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.redis.RedisDao;
import com.pawpawlog.global.util.HttpResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisDao redisDao;
  private final ObjectMapper objectMapper;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final AntPathMatcher pathMatcher = new AntPathMatcher();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return Arrays.stream(SecurityConfig.WHITE_LIST)
        .anyMatch(pattern -> pathMatcher.match(pattern, path));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = resolveToken(request);

    if (StringUtils.hasText(token)) {
      if (redisDao.isBlacklisted(token)) {
        log.warn("블랙리스트 토큰 접근 시도");
        HttpResponseUtil.writeErrorResponse(response, ErrorCode.INVALID_TOKEN, objectMapper);
        return;
      }

      try {
        if (jwtTokenProvider.validateToken(token)) {
          Authentication authentication = jwtTokenProvider.getAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.debug("Security Context 인증 저장 완료. id: {}", authentication.getName());
        }
      } catch (CustomException e) {
        request.setAttribute("exception", e);
      }
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }
}