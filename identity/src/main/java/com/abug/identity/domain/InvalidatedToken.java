package com.abug.identity.domain;

import java.time.Instant;

public class InvalidatedToken {
    private String id;
    private Instant expiryTime;

    private InvalidatedToken(){};

    private InvalidatedToken(String id, Instant expiryTime){
        this.id = id;
        this.expiryTime = expiryTime;
    }

    public static InvalidatedToken create(String id, Instant expiryTime){
        return new InvalidatedToken(id, expiryTime);
    }

    public String getId() {
        return id;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }
}
