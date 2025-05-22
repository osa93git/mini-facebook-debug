package com.ossowski.backend.post.dto;

import com.ossowski.backend.post.model.MediaType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostDto (
    UUID id,
    String text,
    String mediaUrl,
    MediaType mediaType,
    UUID authorId,
    LocalDateTime createdAt
    
){}
