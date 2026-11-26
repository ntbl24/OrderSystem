package com.abug.chat_service.application.port.out;

import com.abug.chat_service.domain.valueobject.UserId;

public interface ProfileUserPort {

    Snapshot getProfile(UserId userId);

    record Snapshot(
            String userId,
            String username,
            String firstName,
            String lastName,
            String avatar
    ){};
}
