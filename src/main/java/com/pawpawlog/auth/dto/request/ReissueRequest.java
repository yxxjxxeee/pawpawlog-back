package com.pawpawlog.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
    @NotBlank(message = "refresh 토큰을 입력해주세요.") String refreshToken
) {

}