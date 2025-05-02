package com.ossowski.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ossowski.backend.model.dto.UserResponseDto;
import com.ossowski.backend.model.mapper.UserMapper;
import com.ossowski.backend.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers(){
     return userService.getAllUsers()
     .stream()
     .map(UserMapper::toDto)
     .toList();   
    }





}
