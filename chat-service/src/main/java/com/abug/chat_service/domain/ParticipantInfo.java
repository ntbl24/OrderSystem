package com.abug.chat_service.domain;

import com.abug.chat_service.domain.valueobject.UserId;

public class ParticipantInfo {
    private UserId userId;
    private String username;
    private String firstName;
    private String lastName;
    private String avatar;

    private ParticipantInfo(UserId userId, String username, String firstName, String lastName, String avatar) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
    }

    public static ParticipantInfo create(UserId userId, String username, String firstName, String lastName, String avatar){
        return new ParticipantInfo(userId, username, firstName, lastName, avatar);
    }


    public UserId getUserId() {
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
