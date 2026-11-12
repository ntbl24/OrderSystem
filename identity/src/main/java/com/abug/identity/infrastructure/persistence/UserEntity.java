package com.abug.identity.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @ManyToMany
    private Set<RoleEntity> roles;
}
