package com.abug.profile.application.usecase;

import com.abug.profile.application.port.in.CreateProfileCommand;
import com.abug.profile.application.port.out.ProfileRepository;
import com.abug.profile.common.exception.AppException;
import com.abug.profile.common.exception.ErrorCode;
import com.abug.profile.domain.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {
    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void createProfile_whenUserDoesNotHaveOne_shouldCreateProfile() {
        UUID userId = UUID.randomUUID();
        when(profileRepository.existsByUserId(userId)).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Profile result = createProfileService.createProfile(
                new CreateProfileCommand(userId, "jane", "jane@example.com"));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("jane");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void createProfile_whenProfileAlreadyExists_shouldThrowConflict() {
        UUID userId = UUID.randomUUID();
        when(profileRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.createProfile(
                new CreateProfileCommand(userId, "jane", "jane@example.com")))
                .isInstanceOfSatisfying(AppException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PROFILE_ALREADY_EXISTS));

        verify(profileRepository).existsByUserId(userId);
    }
}
