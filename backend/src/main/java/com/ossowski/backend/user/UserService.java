package com.ossowski.backend.user;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserPublicDto> getAllUsers(){
        return userRepository.findAll()
        .stream()
        .map(userMapper::toPublicDto)
        .toList();
    }

    public UserPublicDto getUserById(UUID id){
        return userRepository.findById(id)
        .map(userMapper::toPublicDto)
        .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
