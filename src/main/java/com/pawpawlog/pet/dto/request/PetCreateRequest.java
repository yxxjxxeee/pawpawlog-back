package com.pawpawlog.pet.dto.request;

import com.pawpawlog.pet.entity.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PetCreateRequest(
    @NotNull
    @Schema(description = "반려동물 종류", requiredMode = Schema.RequiredMode.REQUIRED)
    PetType petType
) {

}
