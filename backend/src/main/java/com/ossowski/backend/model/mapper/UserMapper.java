package com.ossowski.backend.model.mapper;

import com.ossowski.backend.model.dto.UserResponseDto;
import com.ossowski.backend.model.entity.User;

public class UserMapper{

    public static UserResponseDto toDto(User user){
        return UserResponseDto.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .build();
    }
}