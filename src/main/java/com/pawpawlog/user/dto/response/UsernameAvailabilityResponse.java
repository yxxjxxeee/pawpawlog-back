package com.pawpawlog.user.dto.response;

public record UsernameAvailabilityResponse(boolean available) {

  public static UsernameAvailabilityResponse from(boolean exists) {
    return new UsernameAvailabilityResponse(!exists);
  }
}