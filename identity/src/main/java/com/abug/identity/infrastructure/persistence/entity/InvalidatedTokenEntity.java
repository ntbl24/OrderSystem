package com.abug.identity.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvalidatedTokenEntity {
    @Id
    @Column(nullable = false)
    private String id;

    @Column(name = "expiry_time", nullable = false)
    private Instant expiryTime;
}
