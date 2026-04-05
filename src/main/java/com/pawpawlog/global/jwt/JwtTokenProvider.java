package com.pawpawlog.global.jwt;

import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.redis.RedisDao;
import com.pawpawlog.global.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final String AUTH_CLAIM = "auth";

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.access-expiration}")
  private long accessExpiration;

  @Value("${jwt.refresh-expiration}")
  private long refreshExpiration;

  private Key key;

  private final UserDetailsService userDetailsService;
  private final RedisDao redisDao;

  @PostConstruct
  public void init() {
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public JwtToken generateToken(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String id = String.valueOf(userDetails.getUserId());
    String authorities = extractAuthorities(authentication.getAuthorities());

    return createTokenPair(id, authorities);
  }

  public JwtToken reissueToken(String id) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(id);
    String authorities = extractAuthorities(userDetails.getAuthorities());

    return createTokenPair(id, authorities);
  }

  public Authentication getAuthentication(String accessToken) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(accessToken).getBody();

    if (claims.get(AUTH_CLAIM) == null) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    Collection<? extends GrantedAuthority> authorities = Arrays.stream(
            claims.get(AUTH_CLAIM).toString().split(","))
        .map(SimpleGrantedAuthority::new)
        .toList();

    UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT Token");
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Invalid JWT Token: {}", e.getMessage());
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

  public void validateTokenIgnoreExpiry(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    } catch (ExpiredJwtException ignored) {
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Invalid JWT Token on logout: {}", e.getMessage());
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

  public String validateRefreshTokenAndGetId(String token) {
    Claims claims;
    try {
      claims = Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    String id = claims.getSubject();
    String savedToken = redisDao.getRefreshToken(id);

    if (savedToken == null) {
      throw new CustomException(ErrorCode.MISSING_TOKEN);
    }
    if (!token.equals(savedToken)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    return id;
  }

  public String getIdFromToken(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token).getBody().getSubject();
    } catch (ExpiredJwtException e) {
      return e.getClaims().getSubject();
    }
  }

  public long getRemainingExpiry(String token) {
    try {
      Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token).getBody().getExpiration();
      return expiration.getTime() - System.currentTimeMillis();
    } catch (ExpiredJwtException e) {
      return 0;
    }
  }


  private JwtToken createTokenPair(String id, String authorities) {
    long now = System.currentTimeMillis();
    String accessToken = generateAccessToken(id, authorities, new Date(now + accessExpiration));
    String refreshToken = generateRefreshToken(id, new Date(now + refreshExpiration));

    redisDao.saveRefreshToken(id, refreshToken, refreshExpiration);

    return JwtToken.of(accessToken, refreshToken);
  }

  private String generateAccessToken(String id, String authorities, Date expireDate) {
    return Jwts.builder()
        .setSubject(id)
        .claim(AUTH_CLAIM, authorities)
        .setIssuedAt(new Date())
        .setExpiration(expireDate)
        .signWith(key)
        .compact();
  }

  private String generateRefreshToken(String id, Date expireDate) {
    return Jwts.builder()
        .setSubject(id)
        .setIssuedAt(new Date())
        .setExpiration(expireDate)
        .signWith(key)
        .compact();
  }

  private String extractAuthorities(Collection<? extends GrantedAuthority> authorities) {
    return authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
  }
}