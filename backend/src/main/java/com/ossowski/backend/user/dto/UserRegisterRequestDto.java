package com.ossowski.backend.user.dto;

public record UserRegisterRequestDto(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
