package com.abug.identity.application.port.in;

import com.abug.identity.domain.Role;

import java.util.List;

public interface RoleUseCase {
    Role create(RoleCommand command);
    List<Role> getAll();
    void delete(String role);
}
