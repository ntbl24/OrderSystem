package io.order.backend.user.domain;


import io.order.backend.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;   

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    
}
