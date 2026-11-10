package com.abug.identity.infrastructure.web.controller;

import com.abug.identity.application.port.in.PermissionUseCase;
import com.abug.identity.common.response.ApiResponse;
import com.abug.identity.domain.Permission;
import com.abug.identity.infrastructure.web.dto.PermissionRequest;
import com.abug.identity.infrastructure.web.dto.PermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private  final PermissionUseCase permissionUseCase;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.success(PermissionResponse.from(permissionUseCase.create(request.toCommand())));
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.success(permissionUseCase.getAll().stream().map(PermissionResponse::from).toList());
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission) {
        permissionUseCase.delete(permission);
        return ApiResponse.success(null);
    }
}
