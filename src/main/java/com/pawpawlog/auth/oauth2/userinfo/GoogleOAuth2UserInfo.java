package com.pawpawlog.auth.oauth2.userinfo;

import com.pawpawlog.user.entity.Provider;
import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

  private final Map<String, Object> attributes;

  public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public Provider getProvider() {
    return Provider.GOOGLE;
  }

  @Override
  public String getProviderId() {
    return (String) attributes.get("sub");
  }

  @Override
  public String getNickname() {
    return (String) attributes.get("name");
  }

  @Override
  public String getProfileImageUrl() {
    return (String) attributes.get("picture");
  }
}
