package com.pawpawlog.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

    @Schema(description = "아이디 (영문자 / 숫자만 허용, 4 ~ 50자)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "아이디는 필수값입니다.")
    @Size(min = 4, max = 50, message = "아이디는 4자 이상 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문자와 숫자만 입력할 수 있습니다.")
    String username,

    @Schema(description = "비밀번호 (영문자 / 숫자 / @ 또는 ! 포함, 8 ~ 20자)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@!])[a-zA-Z0-9@!]{8,20}$",
        message = "비밀번호는 영문자, 숫자, @ 또는 ! 특수문자를 포함한 8자 이상 20자 이하로 입력해주세요."
    )
    String password,

    @Schema(description = "닉네임 (2 ~ 30자)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "닉네임은 필수값입니다.")
    @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요.")
    String nickname
) {

}