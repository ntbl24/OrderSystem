package com.abug.identity.application.port.out;

import com.abug.identity.domain.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role permission);
    List<Role> findAll();
    void deleteById(String id);
    Optional<Role> findById(String id);
    List<Role> findAllById(List<String> roles);
}
