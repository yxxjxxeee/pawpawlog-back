package com.pawpawlog.auth.oauth2;

import com.pawpawlog.auth.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.pawpawlog.auth.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.pawpawlog.auth.oauth2.userinfo.NaverOAuth2UserInfo;
import com.pawpawlog.auth.oauth2.userinfo.OAuth2UserInfo;
import com.pawpawlog.user.entity.User;
import com.pawpawlog.user.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserService userService;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2UserInfo userInfo = resolveUserInfo(registrationId, attributes);
    User user = saveOrUpdate(userInfo);

    return new CustomOAuth2User(user, attributes);
  }

  private OAuth2UserInfo resolveUserInfo(String registrationId, Map<String, Object> attributes) {
    return switch (registrationId) {
      case "google" -> new GoogleOAuth2UserInfo(attributes);
      case "kakao" -> new KakaoOAuth2UserInfo(attributes);
      case "naver" -> new NaverOAuth2UserInfo(attributes);
      default -> throw new OAuth2AuthenticationException(
          new OAuth2Error(
              "unsupported_provider",
              "지원하지 않는 소셜 로그인 제공자: " + registrationId,
              null));
    };
  }

  private User saveOrUpdate(OAuth2UserInfo userInfo) {
    return userService.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
        .map(user -> {
          user.updateProfileImage(userInfo.getProfileImageUrl());
          return user;
        })
        .orElseGet(() -> createUser(userInfo));
  }

  private User createUser(OAuth2UserInfo userInfo) {
    log.debug("신규 OAuth2 유저 생성: provider={}, providerId={}", userInfo.getProvider(), userInfo.getProviderId());
    return userService.registerOAuth2User(
        userInfo.getNicknameOrDefault(),
        userInfo.getProvider(),
        userInfo.getProviderId(),
        userInfo.getProfileImageUrl()
    );
  }
}
