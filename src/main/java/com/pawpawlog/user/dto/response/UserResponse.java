package com.pawpawlog.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pawpawlog.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
    @Schema(description = "사용자 ID") Long id,
    @Schema(description = "아이디") String username,
    @Schema(description = "닉네임") String nickname,
    @Schema(description = "프로필 이미지 URL") String profileImageUrl
) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getNickname(),
        user.getProfileImageUrl()
    );
  }
}