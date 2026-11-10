package com.abug.identity.infrastructure.web.controller;

import com.abug.identity.application.port.in.UserUseCase;
import com.abug.identity.common.response.ApiResponse;
import com.abug.identity.domain.UserId;
import com.abug.identity.infrastructure.web.dto.UserCreationRequest;
import com.abug.identity.infrastructure.web.dto.UserResponse;
import com.abug.identity.infrastructure.web.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserUseCase userUseCase;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.success(UserResponse.from(userUseCase.create(request.toCommand())));
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.success(userUseCase.getUsers().stream().map(UserResponse::from).toList());
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.success(UserResponse.from(userUseCase.getUser(UserId.of(userId))));
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.success(UserResponse.from(userUseCase.getMyInfo()));
    }

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
