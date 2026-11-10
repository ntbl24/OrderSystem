package com.abug.identity.application.port.in;

import com.abug.identity.domain.User;
import com.abug.identity.domain.UserId;

import java.util.List;

public interface UserUseCase {
    User create(CreateUserCommand command);
    User getMyInfo();
    User updateUser(UserId userId, UpdateUserCommand command);
    void deleteUser(UserId id);
    List<User> getUsers();
    User getUser(UserId id);
}
