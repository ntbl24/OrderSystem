package io.order.backend.user.domain;


import io.order.backend.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;   

@Data
@EqualsAndHashCode(callSuper = true)  
public class User extends BaseEntity {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    
}
