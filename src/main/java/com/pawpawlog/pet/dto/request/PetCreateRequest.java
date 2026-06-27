package com.pawpawlog.pet.dto.request;

import com.pawpawlog.pet.entity.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PetCreateRequest(
    @NotNull
    @Schema(description = "반려동물 종류", requiredMode = Schema.RequiredMode.REQUIRED)
    PetType petType,

    @Size(max = 50)
    @Schema(description = "반려동물 이름 (최대 50자)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String name,

    @Schema(description = "반려동물 생일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate birthDate
) {

}
