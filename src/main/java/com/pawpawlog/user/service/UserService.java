package com.pawpawlog.user.service;

import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.user.dto.request.SignUpRequest;
import com.pawpawlog.user.dto.response.UserResponse;
import com.pawpawlog.user.dto.response.UsernameAvailabilityResponse;
import com.pawpawlog.user.entity.Provider;
import com.pawpawlog.user.entity.User;
import com.pawpawlog.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UsernameAvailabilityResponse checkUsernameAvailability(String username) {
    return UsernameAvailabilityResponse.from(userRepository.existsByUsername(username));
  }

  public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
    return userRepository.findByProviderAndProviderId(provider, providerId);
  }

  @Transactional
  public UserResponse signUp(SignUpRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    User user = User.builder()
        .username(request.username())
        .password(passwordEncoder.encode(request.password()))
        .nickname(request.nickname())
        .build();

    return UserResponse.from(userRepository.save(user));
  }

  @Transactional
  public User registerOAuth2User(String nickname, Provider provider, String providerId,
      String profileImageUrl) {
    return userRepository.save(User.builder()
        .nickname(nickname)
        .provider(provider)
        .providerId(providerId)
        .profileImageUrl(profileImageUrl)
        .build());
  }
}