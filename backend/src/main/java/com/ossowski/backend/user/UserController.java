package com.ossowski.backend.user;

import java.util.List;
import java.util.UUID;

import com.ossowski.backend.user.dto.*;
import com.ossowski.backend.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserPublicDto> register(@RequestBody UserRegisterRequestDto userToRegister) {
        UserPublicDto newUser = userService.registerUser(userToRegister);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/public")
    public List<UserPublicDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public UserPublicDto getUserById(@PathVariable("id") UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserPublicDto getCurrentUser() {
        User user = userService.getCurrentUser();
        return userMapper.toPublicDto(user);
    }

    @PatchMapping("/me/privacy")
    @PreAuthorize("hasRole('USER')")
    public UserPublicDto updatePrivacy(@RequestBody UserPrivacyRequestDto dto){
        return userService.updatePrivacy(dto.profilePublic());
    }

    @PatchMapping("/me/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDto dto){
        userService.changePassword(dto.oldPassword(), dto.newPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteUserById(){
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/search")
    public List<UserPublicDto> searchPublicUsers(@RequestParam("query") String query) {
        return userService.searchPublicUsers(query);
    }
}
