package com.pawpawlog.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsernameAvailabilityResponse(
    @Schema(description = "아이디 사용 가능 여부") boolean available
) {

  public static UsernameAvailabilityResponse from(boolean exists) {
    return new UsernameAvailabilityResponse(!exists);
  }
}