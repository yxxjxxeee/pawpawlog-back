package com.pawpawlog.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pawpawlog.user.entity.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
    Long id,
    String username,
    String nickname,
    String tag,
    String profileImageUrl
) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getNickname(),
        user.getTag(),
        user.getProfileImageUrl()
    );
  }
}