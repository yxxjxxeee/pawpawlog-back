package com.pawpawlog.auth.oauth2.userinfo;

import com.pawpawlog.user.entity.Provider;

public interface OAuth2UserInfo {

  Provider getProvider();

  String getProviderId();

  String getNickname();

  String getProfileImageUrl();
}
