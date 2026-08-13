package com.pawpawlog.diary.entity;

import com.pawpawlog.global.domain.BaseEntity;
import com.pawpawlog.pet.entity.Pet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "diaries",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pet_id", "diary_date"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diary extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pet_id", nullable = false)
  private Pet pet;

  @Column(name = "diary_date", nullable = false)
  private LocalDate diaryDate;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DiaryEmotion emotion;

  public void updateContent(String content) {
    this.content = content;
  }

  public void updateEmotion(DiaryEmotion emotion) {
    this.emotion = emotion;
  }
}
