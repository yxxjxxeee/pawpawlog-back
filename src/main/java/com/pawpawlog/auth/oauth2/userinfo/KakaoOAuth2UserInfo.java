package com.pawpawlog.auth.oauth2.userinfo;

import com.pawpawlog.user.entity.Provider;
import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

  private final Map<String, Object> attributes;
  private final Map<String, Object> kakaoAccount;
  private final Map<String, Object> profile;

  @SuppressWarnings("unchecked")
  public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
    this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    this.profile = (kakaoAccount != null)
        ? (Map<String, Object>) kakaoAccount.get("profile")
        : Map.of();
  }

  @Override
  public Provider getProvider() {
    return Provider.KAKAO;
  }

  @Override
  public String getProviderId() {
    return String.valueOf(attributes.get("id"));
  }

  @Override
  public String getNickname() {
    return (String) profile.getOrDefault("nickname", "사용자");
  }

  @Override
  public String getProfileImageUrl() {
    return (String) profile.get("profile_image_url");
  }
}
