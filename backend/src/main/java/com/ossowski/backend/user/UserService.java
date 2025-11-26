package com.ossowski.backend.user;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ossowski.backend.exceptions.user.UserCredentialsInvalidException;
import com.ossowski.backend.exceptions.user.UserEmailAlreadyInUseException;
import com.ossowski.backend.exceptions.user.UserNotFoundException;
import com.ossowski.backend.security.service.CurrentUserService;
import com.ossowski.backend.user.dto.UserRegisterRequestDto;
import com.ossowski.backend.user.dto.UserMapper;
import com.ossowski.backend.user.dto.UserPublicDto;
import com.ossowski.backend.user.dto.UserUpdateRequestDto;
import com.ossowski.backend.user.model.Role;
import com.ossowski.backend.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    public UserPublicDto registerUser(UserRegisterRequestDto userToRegister) {
        if (userRepository.existsByEmail(userToRegister.email())) {
            throw new UserEmailAlreadyInUseException(userToRegister.email());
        }

        User newUser = new User(
                userToRegister.firstName(),
                userToRegister.lastName(),
                userToRegister.email(),
                passwordEncoder.encode(userToRegister.password())
        );

        newUser.setRoles(Set.of(Role.USER));

        User savedUser = userRepository.save(newUser);
        return userMapper.toPublicDto(savedUser);
    }


    //====================
    // PublicUsers
    //====================

    public List<UserPublicDto> getAllUsers() {
        return userRepository.findByProfilePublicTrue()
                .stream()
                .map(userMapper::toPublicDto)
                .toList();
    }

    public UserPublicDto getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toPublicDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException(email));
//    }


    public User getCurrentUser(){
        String email = currentUserService.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    public UserPublicDto updateCurrentUser(UserUpdateRequestDto dto) {
        User user = getCurrentUser();

        if(dto.firstName() != null && !dto.firstName().isBlank()) {
            user.setFirstName(dto.firstName().trim());
        }

        if(dto.lastName() != null && !dto.lastName().isBlank()) {
            user.setLastName(dto.lastName().trim());
        }

        if(dto.profilePhotoUrl() != null && !dto.profilePhotoUrl().isBlank()) {
            user.setProfilePhotoUrl(dto.profilePhotoUrl().trim());
        }

        if(dto.bio() != null) {
            user.setBio(dto.bio());
        }

        User saved = userRepository.save(user);
        return userMapper.toPublicDto(saved);
    }

    public UserPublicDto updatePrivacy(boolean profilePublic){
        User user = getCurrentUser();
        user.setProfilePublic(profilePublic);
        User saved = userRepository.save(user);
        return userMapper.toPublicDto(saved);
    }

    public void changePassword(String oldPassword, String newPassword) {
        String email = currentUserService.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserCredentialsInvalidException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    public void deleteCurrentUser(){
        User user = getCurrentUser();
        userRepository.delete(user);
    }

    public List<UserPublicDto> searchPublicUsers(String query) {
        if(query == null || query.isEmpty()) {
            return getAllUsers();
        }

        String q = query.trim();
        return userRepository
                .findByProfilePublicTrueAndFirstNameContainingIgnoreCaseOrProfilePublicTrueAndLastNameContainingIgnoreCase(q, q)
                .stream()
                .map(userMapper::toPublicDto)
                .toList();
    }
}
