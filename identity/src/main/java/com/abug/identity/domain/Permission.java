package com.abug.identity.domain;

public class Permission {
    private String name;
    private String description;

    private Permission(String name, String description){
        this.name = name;
        this.description = description;
    }

    private Permission(){};

    public static Permission create(String name, String description){
        return new Permission(name, description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
