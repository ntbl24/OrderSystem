package com.abug.identity.application.port.out;

import com.abug.identity.domain.InvalidatedToken;

public interface InvalidatedTokenRepository {
    boolean existsById(String id);
    InvalidatedToken save(InvalidatedToken invalidatedToken);
}
