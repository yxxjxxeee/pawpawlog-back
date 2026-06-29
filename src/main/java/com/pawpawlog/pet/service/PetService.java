package com.pawpawlog.pet.service;

import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.pet.dto.request.PetCreateRequest;
import com.pawpawlog.pet.dto.request.PetUpdateRequest;
import com.pawpawlog.pet.dto.response.PetResponse;
import com.pawpawlog.pet.entity.Pet;
import com.pawpawlog.pet.repository.PetRepository;
import com.pawpawlog.user.entity.User;
import com.pawpawlog.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

  private static final int MAX_PET_COUNT = 5;

  private final PetRepository petRepository;
  private final UserRepository userRepository;

  @Transactional
  public PetResponse register(Long userId, PetCreateRequest request) {
    User user = getUser(userId);
    long count = petRepository.countByUser(user);
    if (count >= MAX_PET_COUNT) {
      throw new CustomException(ErrorCode.PET_LIMIT_EXCEEDED);
    }
    Pet pet = Pet.builder()
        .user(user)
        .petType(request.petType())
        .isCurrent(count == 0)
        .build();
    return PetResponse.from(petRepository.save(pet));
  }

  public List<PetResponse> getAll(Long userId) {
    User user = getUser(userId);
    return petRepository.findAllByUser(user).stream()
        .map(PetResponse::from)
        .toList();
  }

  @Transactional
  public PetResponse designateCurrent(Long userId, Long petId) {
    User user = getUser(userId);
    Pet target = petRepository.findByIdAndUser(petId, user)
        .orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));
    petRepository.findAllByUser(user).stream()
        .filter(Pet::isCurrent)
        .forEach(p -> p.updateCurrent(false));
    target.updateCurrent(true);
    return PetResponse.from(target);
  }

  @Transactional
  public PetResponse update(Long userId, Long petId, PetUpdateRequest request) {
    User user = getUser(userId);
    Pet pet = petRepository.findByIdAndUser(petId, user)
        .orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));
    if (request.name() != null) {
      pet.updateName(request.name());
    }
    if (request.birthDate() != null) {
      pet.updateBirthDate(request.birthDate());
    }
    return PetResponse.from(pet);
  }

  @Transactional
  public void delete(Long userId, Long petId) {
    User user = getUser(userId);
    Pet pet = petRepository.findByIdAndUser(petId, user)
        .orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));
    if (pet.isCurrent() && petRepository.countByUser(user) > 1) {
      throw new CustomException(ErrorCode.PET_IS_CURRENT);
    }
    petRepository.delete(pet);
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }
}
