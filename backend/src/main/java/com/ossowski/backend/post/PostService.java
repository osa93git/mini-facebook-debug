package com.ossowski.backend.post;

import java.util.List;
import java.util.UUID;

import com.ossowski.backend.exceptions.post.PostEmptyTextAndUrlException;
import com.ossowski.backend.exceptions.post.PostNotFoundException;
import com.ossowski.backend.post.dto.PostDto;
import com.ossowski.backend.post.dto.PostMapper;
import com.ossowski.backend.post.dto.PostRequestDto;
import com.ossowski.backend.post.model.Post;
import org.springframework.stereotype.Service;

import com.ossowski.backend.user.model.User;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper){
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    public PostDto addPost(PostRequestDto newPostDto, User author){
        Post post = postMapper.createFromRequest(newPostDto, author);
    
        if((post.getText() == null || post.getText().isBlank()) &&
            (post.getMediaUrl() == null || post.getMediaUrl().isBlank())){
                throw new PostEmptyTextAndUrlException();
        }
        Post saved = postRepository.save(post);
        return postMapper.toDto(saved);
    }

    public PostDto getPost(UUID id){
        return postRepository.findById(id)
            .map(postMapper::toDto)
            .orElseThrow(() -> new PostNotFoundException(id));
    }
    public List<PostDto> getAllPosts(){
        return postRepository.findAll().stream()
        .map(postMapper::toDto)
        .toList();
    }

    
}
