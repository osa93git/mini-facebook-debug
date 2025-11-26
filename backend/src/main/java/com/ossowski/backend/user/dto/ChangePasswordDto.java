package com.ossowski.backend.user.dto;

public record ChangePasswordDto(
        String oldPassword,
        String newPassword
) {}
