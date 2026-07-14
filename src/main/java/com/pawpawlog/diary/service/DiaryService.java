package com.pawpawlog.diary.service;

import com.pawpawlog.diary.dto.request.DiaryCreateRequest;
import com.pawpawlog.diary.dto.request.DiaryUpdateRequest;
import com.pawpawlog.diary.dto.response.DiaryResponse;
import com.pawpawlog.diary.entity.Diary;
import com.pawpawlog.diary.repository.DiaryRepository;
import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.pet.entity.Pet;
import com.pawpawlog.pet.repository.PetRepository;
import com.pawpawlog.user.entity.User;
import com.pawpawlog.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

  private final DiaryRepository diaryRepository;
  private final PetRepository petRepository;
  private final UserRepository userRepository;

  @Transactional
  public DiaryResponse create(Long userId, Long petId, DiaryCreateRequest request) {
    Pet pet = getPet(userId, petId);
    if (diaryRepository.existsByPetAndDiaryDate(pet, request.diaryDate())) {
      throw new CustomException(ErrorCode.DIARY_ALREADY_EXISTS);
    }
    Diary diary = Diary.builder()
        .pet(pet)
        .diaryDate(request.diaryDate())
        .content(request.content())
        .emotion(request.emotion())
        .build();
    return DiaryResponse.from(diaryRepository.save(diary));
  }

  public List<DiaryResponse> getMonthly(Long userId, Long petId, YearMonth month) {
    Pet pet = getPet(userId, petId);
    LocalDate start = month.atDay(1);
    LocalDate end = month.atEndOfMonth();
    return diaryRepository.findAllByPetAndDiaryDateBetween(pet, start, end).stream()
        .map(DiaryResponse::from)
        .toList();
  }

  public DiaryResponse getOne(Long userId, Long petId, Long diaryId) {
    return DiaryResponse.from(getDiary(userId, petId, diaryId));
  }

  @Transactional
  public DiaryResponse update(Long userId, Long petId, Long diaryId, DiaryUpdateRequest request) {
    Diary diary = getDiary(userId, petId, diaryId);
    if (request.content() != null) {
      diary.updateContent(request.content());
    }
    if (request.emotion() != null) {
      diary.updateEmotion(request.emotion());
    }
    return DiaryResponse.from(diary);
  }

  @Transactional
  public void delete(Long userId, Long petId, Long diaryId) {
    diaryRepository.delete(getDiary(userId, petId, diaryId));
  }

  private Diary getDiary(Long userId, Long petId, Long diaryId) {
    Pet pet = getPet(userId, petId);
    return diaryRepository.findByIdAndPet(diaryId, pet)
        .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
  }

  private Pet getPet(Long userId, Long petId) {
    User user = getUser(userId);
    return petRepository.findByIdAndUser(petId, user)
        .orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }
}
