package com.pawpawlog.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record OAuth2CodeExchangeRequest(
    @NotBlank(message = "인증 코드는 필수값입니다.")
    @Schema(description = "OAuth2 로그인 콜백에서 발급받은 1회용 코드", requiredMode = Schema.RequiredMode.REQUIRED)
    String code
) {

}
