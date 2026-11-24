package com.abug.profile.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_profiles_user_id", columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(length = 2048)
    private String avatar;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    private LocalDate dob;

    @Column(length = 100)
    private String city;

    @Version
    private Long version;

    public ProfileEntity(
            UUID id,
            UUID userId,
            String avatar,
            String username,
            String email,
            String firstName,
            String lastName,
            LocalDate dob,
            String city,
            Long version) {
        this.id = id;
        this.userId = userId;
        this.avatar = avatar;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.city = city;
        this.version = version;
    }
}
