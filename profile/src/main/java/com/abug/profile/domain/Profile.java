package com.abug.profile.domain;

import java.time.LocalDate;
import java.util.UUID;

public class Profile {
    private final UUID id;
    private final UUID userId;
    private String avatar;
    private final String username;
    private final String email;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String city;
    private Long version;

    private Profile(
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

    public static Profile create(UUID userId, String username, String email) {
        return new Profile(UUID.randomUUID(), userId, null, username, email, null, null, null, null, null);
    }

    public static Profile reconstitute(
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
        return new Profile(id, userId, avatar, username, email, firstName, lastName, dob, city, version);
    }

    public void update(String firstName, String lastName, LocalDate dob, String city) {
        if (firstName != null) {
            this.firstName = firstName;
        }
        if (lastName != null) {
            this.lastName = lastName;
        }
        if (dob != null) {
            this.dob = dob;
        }
        if (city != null) {
            this.city = city;
        }
    }

    public void updateAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getCity() {
        return city;
    }

    public Long getVersion() {
        return version;
    }
}
