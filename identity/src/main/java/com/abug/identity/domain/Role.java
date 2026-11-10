package com.abug.identity.domain;

import java.util.Set;

public class Role {
    private String name;
    private String description;
    private Set<Permission> permissions;

    private Role(){};

    private Role(String name, String description) {
        this.description = description;
        this.name = name;
    }

    private Role(String name, String description, Set<Permission> permissions) {
        this.permissions = permissions;
        this.description = description;
        this.name = name;
    }

    public static Role create(String name, String description){
        return new Role(name, description);
    }

    public static Role create(String name, String description, Set<Permission> permissions){
        return new Role(name, description, permissions);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
