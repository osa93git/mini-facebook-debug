package com.ossowski.backend.user;

import java.util.List;
import java.util.UUID;

import com.ossowski.backend.user.dto.UserMapper;
import com.ossowski.backend.user.dto.UserPublicDto;
import com.ossowski.backend.user.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper){
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserPublicDto> getAllUsers(){
     return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public UserPublicDto getUserById(@PathVariable("id") UUID id){
        return userService.getUserById(id);
    }


    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserPublicDto getCurrentUser(){
        User user = userService.getCurrentUser();
        return userMapper.toPublicDto(user);
    }

}
