package com.abug.identity.infrastructure.web.controller;

import com.abug.identity.application.port.in.RoleUseCase;
import com.abug.identity.common.response.ApiResponse;
import com.abug.identity.infrastructure.web.dto.RoleRequest;
import com.abug.identity.infrastructure.web.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleUseCase roleUseCase;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.success(RoleResponse.from(roleUseCase.create(request.toCommand())));
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.success(roleUseCase.getAll().stream().map(RoleResponse::from).toList());
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role) {
        roleUseCase.delete(role);
        return ApiResponse.success(null);
    }
}