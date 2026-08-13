package com.pawpawlog.diary.dto.request;

import com.pawpawlog.diary.entity.DiaryEmotion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record DiaryCreateRequest(
    @NotNull
    @PastOrPresent
    @Schema(description = "기록 날짜 (오늘 이전 또는 오늘)", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate diaryDate,

    @NotBlank
    @Schema(description = "다이어리 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    String content,

    @NotNull
    @Schema(description = "감정 픽토그램", requiredMode = Schema.RequiredMode.REQUIRED)
    DiaryEmotion emotion
) {

}
