package com.abug.identity.infrastructure.web.controller;

import com.abug.identity.application.dto.IntrospectResult;
import com.abug.identity.application.port.in.AuthenticationUseCase;
import com.abug.identity.common.response.ApiResponse;
import com.abug.identity.infrastructure.web.dto.*;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationUseCase authenticationUseCase;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request){
        String result = authenticationUseCase.authenticate(request.username(), request.password());
        return ApiResponse.success(new AuthenticationResponse(result));
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody @Valid IntrospectRequest request) throws ParseException {
        IntrospectResult result = authenticationUseCase.introspect(request.toCommand());
        return ApiResponse.success(IntrospectResponse.from(result));
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationUseCase.refresh(request.token());
        return ApiResponse.success(new AuthenticationResponse(result));
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationUseCase.logout(request.token());
        return ApiResponse.success(null);
    }
}
