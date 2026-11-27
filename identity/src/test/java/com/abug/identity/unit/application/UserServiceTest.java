package com.abug.identity.unit.application;

import com.abug.event.dto.EventMetadata;
import com.abug.event.dto.NotificationEvent;
import com.abug.identity.application.port.in.CreateUserCommand;
import com.abug.identity.application.port.in.UpdateUserCommand;
import com.abug.identity.application.port.out.CurrentUserPort;
import com.abug.identity.application.port.out.EventPublisher;
import com.abug.identity.application.port.out.RoleRepository;
import com.abug.identity.application.port.out.UserRepository;
import com.abug.identity.application.usecase.UserService;
import com.abug.identity.common.exception.AppException;
import com.abug.identity.common.exception.ErrorCode;
import com.abug.identity.domain.Role;
import com.abug.identity.domain.User;
import com.abug.identity.domain.UserId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CurrentUserPort currentUserPort;

    @Mock
    private EventPublisher publisher;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp(){
        Role mockRole = Role.create("USER", "role for user");

        mockUser = User.reconstitute(
                UserId.of("6b584331-3a7f-4f82-b421-8d59ed2360f0"),
                "customer@example.com", "Test", "encoded-password", false, Set.of(mockRole));
    }

    @Test
    void testGetUsers_ShouldReturnList(){
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        List<User> result = userService.getUsers();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Test", result.getFirst().getUserName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreate_ShouldEncodePasswordAssignUserRoleAndPublishWelcomeEvent() {
        CreateUserCommand command = new CreateUserCommand("customer", "Secure-password!", "customer@example.com");
        Role role = Role.create("USER", "Default user");
        when(roleRepository.findById("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(command.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.create(command);

        assertSame(mockUser, result);
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals(command.email(), saved.getValue().getEmail());
        assertEquals(command.username(), saved.getValue().getUserName());
        assertEquals("encoded-password", saved.getValue().getPassword());
        assertFalse(saved.getValue().isEmailVerified());
        assertEquals(Set.of(role), saved.getValue().getRoles());
        ArgumentCaptor<NotificationEvent> event = ArgumentCaptor.forClass(NotificationEvent.class);
        ArgumentCaptor<EventMetadata> metadata = ArgumentCaptor.forClass(EventMetadata.class);
        verify(publisher).publish(event.capture(), metadata.capture());
        assertDoesNotThrow(() -> java.util.UUID.fromString(event.getValue().eventId()));
        assertEquals("EMAIL", event.getValue().channel());
        assertEquals(mockUser.getEmail(), event.getValue().recipient());
        assertEquals("Welcome to MABB", event.getValue().subject());
        assertEquals("Hello, Test", event.getValue().body());
        assertEquals(new EventMetadata(mockUser.getId().toString(), "USER", "notification-delivery"), metadata.getValue());
        var order = inOrder(userRepository, publisher);
        order.verify(userRepository).save(any(User.class));
        order.verify(publisher).publish(any(), any());
    }

    @Test
    void testCreate_WhenUsernameExists_ShouldRejectWithoutSideEffects() {
        when(userRepository.existsByUsername("customer")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> userService.create(
                new CreateUserCommand("customer", "Secure-password!", "customer@example.com")));

        assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(roleRepository, passwordEncoder, publisher);
    }

    @Test
    void testCreate_WhenDefaultRoleMissing_ShouldNotSaveOrPublish() {
        when(roleRepository.findById("USER")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> userService.create(
                new CreateUserCommand("customer", "Secure-password!", "customer@example.com")));

        assertEquals(ErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, publisher);
    }

    @Test
    void testCreate_WhenSaveFails_ShouldNotPublish() {
        when(roleRepository.findById("USER")).thenReturn(Optional.of(Role.create("USER", "Default user")));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        RuntimeException failure = new IllegalStateException("Database unavailable");
        when(userRepository.save(any())).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> userService.create(
                new CreateUserCommand("customer", "Secure-password!", "customer@example.com"))));

        verifyNoInteractions(publisher);
    }

    @Test
    void testCreate_WhenPublishFails_ShouldPropagateFailure() {
        when(roleRepository.findById("USER")).thenReturn(Optional.of(Role.create("USER", "Default user")));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(mockUser);
        RuntimeException failure = new IllegalStateException("Outbox unavailable");
        doThrow(failure).when(publisher).publish(any(), any());

        assertSame(failure, assertThrows(IllegalStateException.class, () -> userService.create(
                new CreateUserCommand("customer", "Secure-password!", "customer@example.com"))));
        // Transaction rollback belongs in an integration test, not a mocked unit test.
    }

    @Test
    void testUpdateUser_ShouldReplaceRolesEncodePasswordAndPreserveIdentity() {
        Role admin = Role.create("ADMIN", "Administrator");
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(roleRepository.findAllById(List.of("ADMIN"))).thenReturn(List.of(admin));
        when(passwordEncoder.encode("New-password!")).thenReturn("new-encoded-password");
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User result = userService.updateUser(mockUser.getId(), new UpdateUserCommand("New-password!", List.of("ADMIN")));

        assertSame(mockUser, result);
        assertEquals("new-encoded-password", result.getPassword());
        assertEquals(Set.of(admin), result.getRoles());
        assertEquals("customer@example.com", result.getEmail());
        assertEquals("Test", result.getUserName());
        assertEquals(UserId.of("6b584331-3a7f-4f82-b421-8d59ed2360f0"), result.getId());
        verify(userRepository).save(mockUser);
        verifyNoInteractions(publisher);
    }

    @Test
    void testUpdateUser_WhenUserMissing_ShouldNotResolveRolesOrSave() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> userService.updateUser(
                mockUser.getId(), new UpdateUserCommand("New-password!", List.of("USER"))));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verifyNoInteractions(roleRepository, passwordEncoder, publisher);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_WhenAnyRoleMissing_ShouldNotMutateUserOrSave() {
        Set<Role> originalRoles = mockUser.getRoles();
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(roleRepository.findAllById(List.of("USER", "UNKNOWN"))).thenReturn(List.copyOf(originalRoles));

        AppException exception = assertThrows(AppException.class, () -> userService.updateUser(
                mockUser.getId(), new UpdateUserCommand("New-password!", List.of("USER", "UNKNOWN"))));

        assertEquals(ErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
        assertSame(originalRoles, mockUser.getRoles());
        assertEquals("encoded-password", mockUser.getPassword());
        verifyNoInteractions(passwordEncoder, publisher);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetMyInfo_ShouldFindByAuthenticatedUserId() {
        when(currentUserPort.currentUserId()).thenReturn(mockUser.getId().value());
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        assertSame(mockUser, userService.getMyInfo());
        verify(userRepository).findById(mockUser.getId());
    }

    @Test
    void testGetMyInfo_WhenUserMissing_ShouldThrowUserNotFound() {
        when(currentUserPort.currentUserId()).thenReturn(mockUser.getId().value());
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, userService::getMyInfo);

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testGetMyInfo_WhenUnauthenticated_ShouldNotQueryRepository() {
        when(currentUserPort.currentUserId()).thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));

        AppException exception = assertThrows(AppException.class, userService::getMyInfo);

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testGetUser_WhenMissing_ShouldThrowUserNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> userService.getUser(mockUser.getId()));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}
