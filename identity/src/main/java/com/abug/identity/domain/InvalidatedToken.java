package com.abug.identity.domain;

import java.time.Instant;

public class InvalidatedToken {
    private String id;
    private Instant expiryTime;
}
