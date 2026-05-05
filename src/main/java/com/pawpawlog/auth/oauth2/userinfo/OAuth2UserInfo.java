package com.pawpawlog.auth.oauth2.userinfo;

import com.pawpawlog.user.entity.Provider;

public interface OAuth2UserInfo {

  Provider getProvider();

  String getProviderId();

  String getNickname();

  default String getNicknameOrDefault() {
    String nickname = getNickname();
    return (nickname != null && !nickname.isBlank()) ? nickname : "사용자";
  }

  String getProfileImageUrl();
}
