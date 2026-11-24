package com.abug.profile.application.usecase;

import com.abug.profile.application.port.in.UpdateMyProfileCommand;
import com.abug.profile.application.port.out.CurrentUserPort;
import com.abug.profile.application.port.out.ProfileRepository;
import com.abug.profile.domain.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateMyProfileServiceTest {
    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private UpdateMyProfileService updateMyProfileService;

    @Test
    void updateMyProfile_shouldUpdatePersonalDetailsWithoutChangingIdentityFields() {
        UUID userId = UUID.randomUUID();
        Profile profile = Profile.create(userId, "jane", "jane@example.com");
        when(currentUserPort.currentUserId()).thenReturn(userId);
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile result = updateMyProfileService.updateMyProfile(new UpdateMyProfileCommand(
                "Jane", "Doe", LocalDate.of(1990, 5, 1), "Hanoi"));

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getDob()).isEqualTo(LocalDate.of(1990, 5, 1));
        assertThat(result.getCity()).isEqualTo("Hanoi");
        assertThat(result.getUsername()).isEqualTo("jane");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        verify(profileRepository).save(profile);
    }
}
