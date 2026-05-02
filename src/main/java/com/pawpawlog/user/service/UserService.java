package com.pawpawlog.user.service;

import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.user.dto.request.SignUpRequest;
import com.pawpawlog.user.dto.response.UserResponse;
import com.pawpawlog.user.entity.Provider;
import com.pawpawlog.user.entity.User;
import com.pawpawlog.user.repository.UserRepository;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private static final int TAG_MAX_ATTEMPTS = 100;
  private static final int TAG_RANGE = 10000;

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
    return userRepository.findByProviderAndProviderId(provider, providerId);
  }

  @Transactional
  public UserResponse signUp(SignUpRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    String tag = generateUniqueTag(request.nickname());

    User user = User.builder()
        .username(request.username())
        .password(passwordEncoder.encode(request.password()))
        .nickname(request.nickname())
        .tag(tag)
        .build();

    return UserResponse.from(userRepository.save(user));
  }

  @Transactional
  public User registerOAuth2User(String nickname, Provider provider, String providerId,
      String profileImageUrl) {
    String tag = generateUniqueTag(nickname);
    return userRepository.save(User.builder()
        .nickname(nickname)
        .tag(tag)
        .provider(provider)
        .providerId(providerId)
        .profileImageUrl(profileImageUrl)
        .build());
  }

  private String generateUniqueTag(String nickname) {
    for (int i = 0; i < TAG_MAX_ATTEMPTS; i++) {
      String tag = String.format("%04d",
          ThreadLocalRandom.current().nextInt(TAG_RANGE));

      if (!userRepository.existsByNicknameAndTag(nickname, tag)) {
        return tag;
      }
    }
    throw new CustomException(ErrorCode.TAG_GENERATION_FAILED);
  }
}