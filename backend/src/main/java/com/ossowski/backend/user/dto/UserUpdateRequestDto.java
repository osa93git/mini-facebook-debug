package com.ossowski.backend.user.dto;

public record UserUpdateRequestDto( 
        String firstName,
        String lastName,
        String profilePhotoUrl,
        String bio
) {}
