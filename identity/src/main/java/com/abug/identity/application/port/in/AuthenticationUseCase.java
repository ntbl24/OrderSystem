package com.abug.identity.application.port.in;

import com.abug.identity.application.dto.IntrospectResult;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationUseCase {
    IntrospectResult introspect(IntrospectCommand command) throws ParseException;
    String authenticate(String username, String password);
    String refresh(String token) throws ParseException, JOSEException;
    void logout(String token) throws ParseException, JOSEException ;
}
