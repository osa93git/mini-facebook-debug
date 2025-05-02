package com.ossowski.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ossowski.backend.model.entity.User;
import com.ossowski.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}
