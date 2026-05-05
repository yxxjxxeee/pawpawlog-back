package com.pawpawlog.auth.oauth2.userinfo;

import com.pawpawlog.user.entity.Provider;
import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

  private final Map<String, Object> response;

  @SuppressWarnings("unchecked")
  public NaverOAuth2UserInfo(Map<String, Object> attributes) {
    this.response = (Map<String, Object>) attributes.get("response");
  }

  @Override
  public Provider getProvider() {
    return Provider.NAVER;
  }

  @Override
  public String getProviderId() {
    return (String) response.get("id");
  }

  @Override
  public String getNickname() {
    return (String) response.get("nickname");
  }

  @Override
  public String getProfileImageUrl() {
    return (String) response.get("profile_image");
  }
}
