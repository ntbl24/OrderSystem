package com.abug.chat_service.application.port.out;

import com.abug.chat_service.domain.valueobject.UserId;

public interface IdentityPort {
    Snapshot introspect(String token);

    record Snapshot(boolean valid, UserId userId){};
}
