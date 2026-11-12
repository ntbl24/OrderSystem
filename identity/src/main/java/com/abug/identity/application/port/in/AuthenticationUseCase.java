package com.abug.identity.application.port.in;

import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationUseCase {
    boolean introspect(IntrospectCommand command);
    String authenticate(String username, String password);
    String refresh(String token) throws ParseException, JOSEException;
    void logout(String token) throws ParseException, JOSEException ;
}
