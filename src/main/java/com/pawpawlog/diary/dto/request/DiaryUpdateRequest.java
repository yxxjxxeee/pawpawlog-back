package com.pawpawlog.diary.dto.request;

import com.pawpawlog.diary.entity.DiaryEmotion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record DiaryUpdateRequest(
    @Pattern(regexp = "(?s).*\\S.*", message = "공백일 수 없습니다.")
    @Schema(description = "다이어리 내용", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String content,

    @Schema(description = "감정 픽토그램", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    DiaryEmotion emotion
) {

}
