package com.ossowski.backend.user.dto;

import com.ossowski.backend.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserPublicDto toPublicDto(User user) {
        return new UserPublicDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePhotoUrl(),
                user.getBio()
        );
    }
}
