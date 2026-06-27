package com.pawpawlog.pet.repository;

import com.pawpawlog.pet.entity.Pet;
import com.pawpawlog.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

  List<Pet> findAllByUser(User user);

  long countByUser(User user);

  Optional<Pet> findByIdAndUser(Long id, User user);
}
