package com.ossowski.backend.comment.dto;

import java.util.UUID;

public record CommentRequestDto (
        UUID postId,
        UUID parentId,
        String content
)
{}
