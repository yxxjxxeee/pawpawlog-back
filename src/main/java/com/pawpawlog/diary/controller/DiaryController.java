package com.pawpawlog.diary.controller;

import com.pawpawlog.diary.dto.request.DiaryCreateRequest;
import com.pawpawlog.diary.dto.request.DiaryUpdateRequest;
import com.pawpawlog.diary.dto.response.DiaryResponse;
import com.pawpawlog.diary.service.DiaryService;
import com.pawpawlog.global.response.ErrorResponse;
import com.pawpawlog.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.YearMonth;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Diary", description = "반려동물 다이어리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pets/{petId}/diaries")
public class DiaryController {

  private final DiaryService diaryService;

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "다이어리 작성", description = "반려동물의 하루 기록을 작성합니다. 같은 날짜에 중복 작성할 수 없습니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "작성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiaryResponse.class))),
      @ApiResponse(responseCode = "400", description = "입력값 유효성 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "해당 날짜에 이미 기록 존재", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<DiaryResponse> create(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId,
      @RequestBody @Valid DiaryCreateRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(diaryService.create(userDetails.getUserId(), petId, request));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "월별 다이어리 조회", description = "해당 반려동물의 특정 월 다이어리 목록을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DiaryResponse.class)))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  public ResponseEntity<List<DiaryResponse>> getMonthly(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId,
      @RequestParam YearMonth month
  ) {
    return ResponseEntity.ok(diaryService.getMonthly(userDetails.getUserId(), petId, month));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "다이어리 상세 조회", description = "다이어리 단건을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiaryResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물 또는 다이어리를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{diaryId}")
  public ResponseEntity<DiaryResponse> getOne(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId,
      @PathVariable Long diaryId
  ) {
    return ResponseEntity.ok(diaryService.getOne(userDetails.getUserId(), petId, diaryId));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "다이어리 수정", description = "다이어리의 내용과 감정을 수정합니다. 날짜는 수정할 수 없습니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiaryResponse.class))),
      @ApiResponse(responseCode = "400", description = "입력값 유효성 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물 또는 다이어리를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping("/{diaryId}")
  public ResponseEntity<DiaryResponse> update(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId,
      @PathVariable Long diaryId,
      @RequestBody @Valid DiaryUpdateRequest request
  ) {
    return ResponseEntity.ok(diaryService.update(userDetails.getUserId(), petId, diaryId, request));
  }

  @SecurityRequirement(name = "BearerAuth")
  @Operation(summary = "다이어리 삭제", description = "다이어리를 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "반려동물 또는 다이어리를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{diaryId}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long petId,
      @PathVariable Long diaryId
  ) {
    diaryService.delete(userDetails.getUserId(), petId, diaryId);
    return ResponseEntity.noContent().build();
  }
}