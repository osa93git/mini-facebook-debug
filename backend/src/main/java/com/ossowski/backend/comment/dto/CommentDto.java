package com.ossowski.backend.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto (
    UUID id,
    String content,
    UUID authorId,
    UUID postId,
    UUID parentId,
    LocalDateTime createdAt,
    int likeCount
){}
