package com.pawpawlog.diary.repository;

import com.pawpawlog.diary.entity.Diary;
import com.pawpawlog.pet.entity.Pet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

  Optional<Diary> findByIdAndPet(Long id, Pet pet);

  List<Diary> findAllByPetAndDiaryDateBetween(Pet pet, LocalDate start, LocalDate end);

  boolean existsByPetAndDiaryDate(Pet pet, LocalDate diaryDate);
}