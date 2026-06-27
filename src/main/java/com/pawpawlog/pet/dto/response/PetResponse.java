package com.pawpawlog.pet.dto.response;

import com.pawpawlog.pet.entity.Pet;
import com.pawpawlog.pet.entity.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record PetResponse(
    @Schema(description = "반려동물 ID") Long id,
    @Schema(description = "반려동물 종류") PetType petType,
    @Schema(description = "반려동물 이름") String name,
    @Schema(description = "반려동물 생일") LocalDate birthDate,
    @Schema(description = "대표 펫 여부") boolean isCurrent
) {

  public static PetResponse from(Pet pet) {
    return new PetResponse(
        pet.getId(),
        pet.getPetType(),
        pet.getName(),
        pet.getBirthDate(),
        pet.isCurrent()
    );
  }
}
