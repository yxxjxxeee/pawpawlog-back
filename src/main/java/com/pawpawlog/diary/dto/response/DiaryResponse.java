package com.pawpawlog.diary.dto.response;

import com.pawpawlog.diary.entity.Diary;
import com.pawpawlog.diary.entity.DiaryEmotion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DiaryResponse(
    @Schema(description = "다이어리 ID") Long id,
    @Schema(description = "반려동물 ID") Long petId,
    @Schema(description = "기록 날짜") LocalDate diaryDate,
    @Schema(description = "내용") String content,
    @Schema(description = "감정 픽토그램") DiaryEmotion emotion,
    @Schema(description = "생성일시") LocalDateTime createdAt,
    @Schema(description = "수정일시") LocalDateTime updatedAt
) {

  public static DiaryResponse from(Diary diary) {
    return new DiaryResponse(
        diary.getId(),
        diary.getPet().getId(),
        diary.getDiaryDate(),
        diary.getContent(),
        diary.getEmotion(),
        diary.getCreatedAt(),
        diary.getUpdatedAt()
    );
  }
}