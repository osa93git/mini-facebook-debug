package com.ossowski.backend.user;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ossowski.backend.exceptions.user.UserEmailAlreadyInUseException;
import com.ossowski.backend.exceptions.user.UserNotFoundException;
import com.ossowski.backend.user.dto.UserRegisterRequestDto;
import com.ossowski.backend.user.dto.UserMapper;
import com.ossowski.backend.user.dto.UserPublicDto;
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


    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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

//        newUser.getRoles().add(Role.USER);
        newUser.setRoles(Set.of(Role.USER));

        User savedUser = userRepository.save(newUser);
        return userMapper.toPublicDto(savedUser);
    }


    public List<UserPublicDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toPublicDto)
                .toList();
    }

    public UserPublicDto getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toPublicDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
