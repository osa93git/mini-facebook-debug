package com.ossowski.backend.comment;

import java.util.List;
import java.util.UUID;

import com.ossowski.backend.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostId(UUID postId);
}
