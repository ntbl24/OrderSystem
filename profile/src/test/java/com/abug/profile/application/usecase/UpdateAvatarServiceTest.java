package com.abug.profile.application.usecase;

import com.abug.profile.application.port.out.CurrentUserPort;
import com.abug.profile.application.port.out.ProfileRepository;
import com.abug.profile.domain.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAvatarServiceTest {
    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private UpdateAvatarService updateAvatarService;

//    @Test
//    void updateAvatar_shouldChangeOnlyCurrentUsersAvatar() {
//        UUID userId = UUID.randomUUID();
//        Profile profile = Profile.create(userId, "jane", "jane@example.com");
//        when(currentUserPort.currentUserId()).thenReturn(userId);
//        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
//        when(profileRepository.save(profile)).thenReturn(profile);
//
//        Profile result = updateAvatarService.updateAvatar();
//
//        assertThat(result.getAvatar()).isEqualTo("https://cdn.example.com/avatar.png");
//        assertThat(result.getUsername()).isEqualTo("jane");
//        assertThat(result.getEmail()).isEqualTo("jane@example.com");
//        verify(profileRepository).save(profile);
//    }
}
