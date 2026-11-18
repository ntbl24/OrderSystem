package com.abug.identity.infrastructure.web.controller;

import com.abug.identity.application.port.in.UserUseCase;
import com.abug.identity.common.response.ApiResponse;
import com.abug.identity.domain.UserId;
import com.abug.identity.infrastructure.web.dto.UserCreationRequest;
import com.abug.identity.infrastructure.web.dto.UserResponse;
import com.abug.identity.infrastructure.web.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserUseCase userUseCase;

    @PostMapping("/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.success(UserResponse.from(userUseCase.create(request.toCommand())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.success(userUseCase.getUsers().stream().map(UserResponse::from).toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.success(UserResponse.from(userUseCase.getUser(UserId.of(userId))));
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.success(UserResponse.from(userUseCase.getMyInfo()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userUseCase.deleteUser(UserId.of(userId));
        return ApiResponse.success(null);
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(UserResponse.from(userUseCase.updateUser(UserId.of(userId), request.toCommand())));
    }
}
