package com.abug.identity.application.port.out;

import com.abug.identity.domain.User;
import com.abug.identity.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User save(User user);
    Optional<User> findById(UserId userId);
    void deleteById(UserId userId);
    List<User> findAll();
}
