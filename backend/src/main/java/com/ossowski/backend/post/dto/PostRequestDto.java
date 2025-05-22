package com.ossowski.backend.post.dto;

import com.ossowski.backend.post.model.MediaType;

public record PostRequestDto (
    String text,
    String mediaUrl,
    MediaType mediaType
    
){}
