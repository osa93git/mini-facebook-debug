package com.ossowski.backend.post;

import java.util.UUID;

import com.ossowski.backend.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, UUID> {
    
}
