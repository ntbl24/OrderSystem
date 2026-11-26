package com.abug.chat_service.infrastructure.external.httpclient.adapter;

import com.abug.chat_service.application.port.out.IdentityPort;
import com.abug.chat_service.domain.valueobject.UserId;
import com.abug.chat_service.infrastructure.external.httpclient.IdentityClient;
import com.abug.chat_service.infrastructure.external.httpclient.dto.IntrospectRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdentityAdapter implements IdentityPort {
    private final IdentityClient identityClient;

    public Snapshot introspect(String token){
        try{
            var result = identityClient.introspect(new IntrospectRequest(token)).getData();
            if(Objects.isNull(result)){
                return null;
            }
            return new Snapshot(result.valid(), UserId.of(result.userId()));
        } catch (FeignException e){
            log.info("Introspect failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
