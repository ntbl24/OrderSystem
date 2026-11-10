package com.abug.identity.domain;


import com.abug.identity.common.constant.PredefinedRole;

import java.util.Set;

public class User {
    private UserId id;
    private String email;
    private String userName;
    private String password;
    private boolean emailVerified;
    private Set<Role> roles;

    private User(){}

    private User(UserId id, String email, String userName, String password, boolean emailVerified, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.emailVerified = emailVerified;
        this.roles = roles;
    }

    public static User create(String email, String userName, String password, boolean emailVerified, Set<Role> roles){
        return new User(UserId.generate(), email, userName, password, emailVerified, roles);
    }

    public static User reconstitute(UserId id, String email, String userName, String password, boolean emailVerified, Set<Role> roles) {
        return new User(id, email, userName, password, emailVerified, roles);
    }

    public void update(String email, String password, Set<Role> roles){
        this.email = email != null ? email : this.email;
        this.password = password != null ? password : this.password;
        this.roles = roles != null ? roles : this.roles;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    public UserId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
