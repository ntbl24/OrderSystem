package com.abug.gateway.service;

import com.abug.gateway.client.IdentityClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentityService {
    private final IdentityClient identityClient;
    public boolean introspect(String token){

    }
}
