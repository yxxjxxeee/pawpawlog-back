package com.pawpawlog.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PetUpdateRequest(
    @Size(max = 50)
    @Pattern(regexp = "(?s).*\\S.*", message = "공백일 수 없습니다.")
    @Schema(description = "반려동물 이름 (최대 50자)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String name,

    @PastOrPresent
    @Schema(description = "반려동물 생일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate birthDate
) {

}
