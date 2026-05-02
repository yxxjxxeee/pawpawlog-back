package com.pawpawlog.global.security;

import com.pawpawlog.user.entity.User;
import com.pawpawlog.user.entity.UserStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean accountNonLocked;
  private final boolean enabled;

  public CustomUserDetails(User user) {
    this.userId = user.getId();
    this.password = user.getPassword();
    this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    this.accountNonLocked = user.getStatus() != UserStatus.SUSPENDED;
    this.enabled = user.getStatus() != UserStatus.DELETED;
  }

  private CustomUserDetails(Long userId, Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.password = null;
    this.authorities = authorities;
    this.accountNonLocked = true;
    this.enabled = true;
  }

  public static CustomUserDetails fromClaims(Long userId,
      Collection<? extends GrantedAuthority> authorities) {
    return new CustomUserDetails(userId, authorities);
  }

  public Long getUserId() {
    return userId;
  }

  @Override
  public String getUsername() {
    return String.valueOf(userId);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}
