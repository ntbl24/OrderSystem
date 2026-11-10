package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.domain.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        String id,
        String email,
        String username,
        Set<RoleResponse>roles
) {
    public static UserResponse from(User user){
        return new UserResponse(user.getId().toString(), user.getEmail(), user.getUserName(), user.getRoles().stream().map(RoleResponse::from).collect(Collectors.toSet()));
    }
}
