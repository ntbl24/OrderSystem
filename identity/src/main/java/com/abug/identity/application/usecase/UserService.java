package com.abug.identity.application.usecase;

import com.abug.identity.application.port.in.CreateUserCommand;
import com.abug.identity.application.port.in.UpdateUserCommand;
import com.abug.identity.application.port.in.UserUseCase;
import com.abug.identity.application.port.out.RoleRepository;
import com.abug.identity.application.port.out.UserRepository;
import com.abug.identity.common.constant.PredefinedRole;
import com.abug.identity.common.exception.AppException;
import com.abug.identity.common.exception.ErrorCode;
import com.abug.identity.domain.Role;
import com.abug.identity.domain.User;
import com.abug.identity.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public User create(CreateUserCommand command) {

        if (userRepository.existsByUsername(command.username())) throw new AppException(ErrorCode.USER_EXISTED);

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        User user = User.create(command.email(), command.username(), passwordEncoder.encode(command.password()), false, roles);

        User newUser = userRepository.save(user);

        kafkaTemplate.send("onboard-successful", "Welcome our new member" + user.getUserName());

        return newUser;
    }

    @Override
    public User getMyInfo() {

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User updateUser(UserId userId, UpdateUserCommand command) {

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Role> roles = roleRepository.findAllById(command.roles());

        user.update(null, passwordEncoder.encode(command.password()), new HashSet<>(roles));


        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UserId userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getUsers() {

        return userRepository.findAll();
    }

    @Override
    public User getUser(UserId id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
