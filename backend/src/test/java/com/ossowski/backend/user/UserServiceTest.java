package com.ossowski.backend.user;

import com.ossowski.backend.exceptions.user.UserCredentialsInvalidException;
import com.ossowski.backend.exceptions.user.UserEmailAlreadyInUseException;
import com.ossowski.backend.exceptions.user.UserNotFoundException;
import com.ossowski.backend.security.service.CurrentUserService;
import com.ossowski.backend.user.dto.UserMapper;
import com.ossowski.backend.user.dto.UserPublicDto;
import com.ossowski.backend.user.dto.UserRegisterRequestDto;
import com.ossowski.backend.user.dto.UserUpdateRequestDto;
import com.ossowski.backend.user.model.Role;
import com.ossowski.backend.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerUser_shouldSaveNewUserWithEncodedPasswordAndUserRole(){
        UserRegisterRequestDto dto = new UserRegisterRequestDto(
                "Piotr", "Ossowski", "piotr@example.com", "plainPass"
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode("plainPass")).thenReturn("encoded");

        User saved = new User("Piotr", "Ossowski", "piotr@example.com", "encoded");
        saved.setRoles(Set.of(Role.USER));
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserPublicDto publicDto = new UserPublicDto(
                UUID.randomUUID(), "Piotr", "Ossowski", saved.getProfilePhotoUrl(), ""
        );
        when(userMapper.toPublicDto(saved)).thenReturn(publicDto);

        UserPublicDto result = userService.registerUser(dto);

        assertThat(result.firstName()).isEqualTo("Piotr");
        assertThat(result.lastName()).isEqualTo("Ossowski");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User toSave = userCaptor.getValue();

        assertThat(toSave.getEmail()).isEqualTo("piotr@example.com");
        assertThat(toSave.getPassword()).isEqualTo("encoded");
        assertThat(toSave.getRoles()).containsExactly(Role.USER);
    }

    @Test
    void registerUser_shouldThrowWhenEmailAlreadyInUse(){

        UserRegisterRequestDto dto = new UserRegisterRequestDto(
          "Piotr", "Ossowski", "piotr@example.com", "plainPass"
        );
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);
        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(UserEmailAlreadyInUseException.class);
    }

    @Test
    void getCurrentUser_shouldUseEmailFromCurrentUserService(){
        String email = "piotr@example.com";

        when(currentUserService.getCurrentUserEmail()).thenReturn(email);

        User user = new User("Piotr", "Ossowski", email, "encoded");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void getCurrentUser_shouldThrowWhenUserNotFound(){
        String email = "missing@example.com";

        when(currentUserService.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UserNotFoundException.class);
    }
    @Test
    void changePassword_shouldUpdatePasswordWhenOldPasswordMatches(){
        String email = "piotr@example.com";

        when(currentUserService.getCurrentUserEmail()).thenReturn(email);

        User user = new User("Piotr", "Ossowski", email, "encodedOld");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("encodedNew");

        userService.changePassword("old", "new");
        assertThat(user.getPassword()).isEqualTo("encodedNew");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrownWhenOldPasswordDoesNotMatch(){
        String email = "piotr@example.com";

        when(currentUserService.getCurrentUserEmail()).thenReturn(email);

        User user = new User("Piotr", "Ossowski", email, "encodedOld");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedOld")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("wrong", "new"))
                .isInstanceOf(UserCredentialsInvalidException.class);
    }

    @Test void updateCurrentUser_shouldTrimAndUpdateFields(){
        String email = "piotr@example.com";

        when(currentUserService.getCurrentUserEmail()).thenReturn(email);

        User user = new User("Piotr", "Old", email, "pass");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserUpdateRequestDto dto = new UserUpdateRequestDto(
                "  Piotr  ",
                " New ",
                "https://photo ",
                "Nowe bio"
        );

        when(userRepository.save(user)).thenReturn(user);
        UserPublicDto publicDto = new UserPublicDto(
               UUID.randomUUID(), "Piotr", "New", "https://photo", "Nowe bio"
        );
        when(userMapper.toPublicDto(user)).thenReturn(publicDto);

        UserPublicDto result = userService.updateCurrentUser(dto);

        assertThat(result.firstName()).isEqualTo("Piotr");
        assertThat(result.lastName()).isEqualTo("New");
        assertThat(result.profilePhotoUrl()).isEqualTo("https://photo");
        assertThat(result.bio()).isEqualTo("Nowe bio");

        assertThat(user.getFirstName()).isEqualTo("Piotr");
        assertThat(user.getLastName()).isEqualTo("New");
        assertThat(user.getProfilePhotoUrl()).isEqualTo("https://photo");
        assertThat(user.getBio()).isEqualTo("Nowe bio");

    }

    @Test
    void updateCurrentUser_shouldThrowWhenUserNotFound(){
        String email = "missing@example.com";

        when(currentUserService.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserUpdateRequestDto dto = new UserUpdateRequestDto(
                "Piotr",
                "Ossowski",
                "https://photo",
                "bio"
        );
        assertThatThrownBy(() -> userService.updateCurrentUser(dto))
                .isInstanceOf(UserNotFoundException.class);
    }
}
