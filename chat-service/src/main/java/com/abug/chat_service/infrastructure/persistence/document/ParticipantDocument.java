package com.abug.chat_service.infrastructure.persistence.document;

import java.util.UUID;

public class ParticipantDocument {
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private String avatar;

    public ParticipantDocument(UUID userId, String username, String firstName, String lastName, String avatar) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatar() {
        return avatar;
    }
}
