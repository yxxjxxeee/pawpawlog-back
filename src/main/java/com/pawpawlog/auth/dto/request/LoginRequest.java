package com.pawpawlog.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(description = "아이디", requiredMode = Schema.RequiredMode.REQUIRED) String username,
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED) String password
) {

}
