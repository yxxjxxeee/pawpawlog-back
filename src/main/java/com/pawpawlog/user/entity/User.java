package com.pawpawlog.user.entity;

import com.pawpawlog.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 50)
  private String username;

  @Column(length = 255)
  private String password;

  @Column(nullable = false, length = 30)
  private String nickname;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private Provider provider;

  @Column(name = "provider_id", length = 50)
  private String providerId;

  @Column(length = 255)
  private String profileImageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private UserRole role = UserRole.USER;

  public void suspend() {
    this.status = UserStatus.SUSPENDED;
  }

  public void activate() {
    this.status = UserStatus.ACTIVE;
  }

  public void delete() {
    this.status = UserStatus.DELETED;
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void updateProfileImage(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  // equals & hashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User other)) {
      return false;
    }
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}