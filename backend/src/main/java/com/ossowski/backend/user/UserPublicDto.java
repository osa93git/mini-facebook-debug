package com.ossowski.backend.user;

import java.util.UUID;

public record UserPublicDto (
    
    UUID id,
    String firstName,
    String lastName,
    String profilePhotoUrl,
    String bio
){}
