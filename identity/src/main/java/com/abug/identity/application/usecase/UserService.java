package com.abug.identity.application.usecase;

import com.abug.event.dto.EventMetadata;
import com.abug.event.dto.NotificationEvent;
import com.abug.identity.application.port.in.CreateUserCommand;
import com.abug.identity.application.port.in.UpdateUserCommand;
import com.abug.identity.application.port.in.UserUseCase;
import com.abug.identity.application.port.out.*;
import com.abug.identity.common.constant.PredefinedRole;
import com.abug.identity.common.exception.AppException;
import com.abug.identity.common.exception.ErrorCode;
import com.abug.identity.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserPort currentUserPort;
    private final EventPublisher publisher;

    @Override
    @Transactional
    public User create(CreateUserCommand command) {
        if (userRepository.existsByUsername(command.username())) throw new AppException(ErrorCode.USER_EXISTED);

        Role userRole = roleRepository.findById(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        HashSet<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.create(command.email(), command.username(), passwordEncoder.encode(command.password()), false, roles);

        User newUser = userRepository.save(user);

        NotificationEvent notificationEvent = new NotificationEvent(
                EventId.generate().toString(),
                "EMAIL",
                newUser.getEmail(),
                null,
                null,
                "Welcome to MABB",
                "Hello, " + newUser.getUserName());

        publisher.publish(notificationEvent,
                new EventMetadata(
                        newUser.getId().toString(),
                        "USER",
                        "notification-delivery")
        );

        return newUser;
    }

    @Override
    public User getMyInfo() {

        UUID userId = currentUserPort.currentUserId();

        return userRepository.findById(UserId.of(userId)).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public User updateUser(UserId userId, UpdateUserCommand command) {

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Role> roles = roleRepository.findAllById(command.roles());

        if (roles.size() != command.roles().size()) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        user.update(null, passwordEncoder.encode(command.password()), new HashSet<>(roles));


        return userRepository.save(user);
    }

    @Override
    @Transactional
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
