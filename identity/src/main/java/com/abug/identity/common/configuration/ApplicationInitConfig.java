package com.abug.identity.common.configuration;

import com.abug.identity.application.port.out.RoleRepository;
import com.abug.identity.application.port.out.UserRepository;
import com.abug.identity.common.constant.PredefinedRole;
import com.abug.identity.domain.Role;
import com.abug.identity.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository){
        return args -> {
            if(userRepository.existsByUsername(ADMIN_USER_NAME)){
                roleRepository.save(Role.create(PredefinedRole.USER_ROLE, "User role"));

                Role adminRole = roleRepository.save(Role.create(PredefinedRole.ADMIN_ROLE, "Admin role"));

                var roles = new HashSet<Role>();

                roles.add(adminRole);

                User user = User.create(null, ADMIN_USER_NAME, passwordEncoder.encode(ADMIN_PASSWORD), true, roles);
                userRepository.save(user);
            }
        };
    }
}
