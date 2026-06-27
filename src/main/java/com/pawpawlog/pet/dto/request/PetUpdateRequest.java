package com.pawpawlog.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PetUpdateRequest(
    @Size(min = 1, max = 50)
    @Schema(description = "반려동물 이름 (최대 50자)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String name,

    @Schema(description = "반려동물 생일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate birthDate
) {

}
