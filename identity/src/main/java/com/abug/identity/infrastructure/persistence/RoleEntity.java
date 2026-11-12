package com.abug.identity.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Table(name = "role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoleEntity {
    @Id
    @Column(nullable = false)
    private String name;
    private String description;

    @ManyToMany
    private Set<PermissionEntity> permissions;
}
