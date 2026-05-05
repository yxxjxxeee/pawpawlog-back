package com.pawpawlog.pet.entity;

import com.pawpawlog.global.domain.BaseEntity;
import com.pawpawlog.user.entity.User;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Pet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PetType petType;

  @Column(nullable = false, length = 50)
  private String name;

  private LocalDate birthDate;

  @Column(nullable = false)
  @Builder.Default
  private boolean isCurrent = false;

  public void updateName(String name) {
    this.name = name;
  }

  public void updateBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public void designateAsCurrent() {
    this.isCurrent = true;
  }

  public void undesignateAsCurrent() {
    this.isCurrent = false;
  }
}