package com.pawpawlog.global.security;

import com.pawpawlog.user.entity.User;
import com.pawpawlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다."));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    CustomUserDetails userDetails = new CustomUserDetails(user);

    if (!userDetails.isAccountNonLocked()) {
      throw new LockedException("정지된 계정입니다.");
    }

    if (!userDetails.isEnabled()) {
      throw new DisabledException("탈퇴한 계정입니다.");
    }

    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
