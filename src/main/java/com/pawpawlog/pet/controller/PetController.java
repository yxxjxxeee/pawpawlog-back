package com.pawpawlog.pet.controller;

import com.pawpawlog.global.response.ErrorResponse;
import com.pawpawlog.global.security.CustomUserDetails;
import com.pawpawlog.pet.dto.request.PetCreateRequest;
import com.pawpawlog.pet.dto.request.PetUpdateRequest;
import com.pawpawlog.pet.dto.response.PetResponse;
import com.pawpawlog.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pet", description = "반려동물 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {

  private final PetService petService;

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "반려동물 등록", description = "반려동물을 등록합니다. 첫 등록 시 대표 펫으로 자동 지정됩니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponse.class))),
      @ApiResponse(responseCode = "400", description = "입력값 유효성 오류 또는 5마리 초과", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<PetResponse> register(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid PetCreateRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(petService.register(userDetails.getUserId(), request));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "반려동물 목록 조회", description = "로그인한 사용자의 반려동물 전체를 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PetResponse.class)))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  public ResponseEntity<List<PetResponse>> getAll(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    return ResponseEntity.ok(petService.getAll(userDetails.getUserId()));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "대표 펫 지정", description = "특정 반려동물을 대표 펫으로 지정합니다. 기존 대표 펫은 자동으로 해제됩니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "지정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/{petId}/current")
  public ResponseEntity<PetResponse> designateCurrent(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId
  ) {
    return ResponseEntity.ok(petService.designateCurrent(userDetails.getUserId(), petId));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "반려동물 정보 수정", description = "반려동물의 이름과 생일을 수정합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponse.class))),
      @ApiResponse(responseCode = "400", description = "입력값 유효성 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping("/{petId}")
  public ResponseEntity<PetResponse> update(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId,
      @RequestBody @Valid PetUpdateRequest request
  ) {
    return ResponseEntity.ok(petService.update(userDetails.getUserId(), petId, request));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "반려동물 삭제", description = "반려동물을 삭제합니다. 대표 펫은 다른 펫이 있을 경우 삭제할 수 없습니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "400", description = "대표 펫은 다른 펫이 있을 경우 삭제 불가", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{petId}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId
  ) {
    petService.delete(userDetails.getUserId(), petId);
    return ResponseEntity.noContent().build();
  }
}
