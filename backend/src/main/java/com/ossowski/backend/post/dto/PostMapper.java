package com.ossowski.backend.post.dto;

import com.ossowski.backend.post.model.Post;
import org.springframework.stereotype.Component;

import com.ossowski.backend.user.model.User;

@Component
public class PostMapper {

    public PostDto toDto(Post post) {
        return new PostDto(
            post.getId(),
            post.getText(),
            post.getMediaUrl(),
            post.getMediaType(),
            post.getAuthor().getId(),
            post.getCreatedAt()
        );
    }

    public Post createFromRequest(PostRequestDto dto, User author){
        Post post = new Post();
        post.setText(dto.text());
        post.setMediaUrl(dto.mediaUrl());
        post.setMediaType(dto.mediaType());
        post.setAuthor(author);
        return post;   
    }





}
