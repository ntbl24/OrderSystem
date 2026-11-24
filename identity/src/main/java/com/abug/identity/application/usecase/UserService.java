package com.abug.identity.application.usecase;

import com.abug.event.dto.NotificationEvent;
import com.abug.identity.application.port.in.CreateUserCommand;
import com.abug.identity.application.port.in.UpdateUserCommand;
import com.abug.identity.application.port.in.UserUseCase;
import com.abug.identity.application.port.out.OutboxRepository;
import com.abug.identity.application.port.out.RoleRepository;
import com.abug.identity.application.port.out.UserRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public User create(CreateUserCommand command) {
        if (userRepository.existsByUsername(command.username())) throw new AppException(ErrorCode.USER_EXISTED);

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        User user = User.create(command.email(), command.username(), passwordEncoder.encode(command.password()), false, roles);

        User newUser = userRepository.save(user);

        EventId eventId = EventId.generate();

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventId(eventId.toString())
                .channel("EMAIL")
                .recipient(user.getEmail())
                .subject("Welcome to MABB")
                .body("Hello, " + user.getUserName())
                .build();

        OutboxEvent outboxEvent = OutboxEvent.create(eventId, user.getId().value(), "User", "UserCreated", objectMapper.writeValueAsString(notificationEvent), false);

        outboxRepository.save(outboxEvent);

        return user;
    }

    @Override
    public User getMyInfo() {

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public User updateUser(UserId userId, UpdateUserCommand command) {

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Role> roles = roleRepository.findAllById(command.roles());

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
