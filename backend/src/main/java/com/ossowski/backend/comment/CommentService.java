package com.ossowski.backend.comment;

import com.ossowski.backend.comment.dto.CommentDto;
import com.ossowski.backend.comment.dto.CommentMapper;
import com.ossowski.backend.comment.dto.CommentRequestDto;
import com.ossowski.backend.comment.model.Comment;
import com.ossowski.backend.post.model.Post;
import com.ossowski.backend.post.PostRepository;

import com.ossowski.backend.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    public CommentDto addComment(CommentRequestDto commentRequestDto, User author){
        if(commentRequestDto.content() == null || commentRequestDto.content().isBlank()){
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        Post post = postRepository.findById(commentRequestDto.postId())
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Comment newComment = new Comment();
        newComment.setAuthor(author);
        newComment.setPost(post);
        newComment.setContent(commentRequestDto.content());

        if(commentRequestDto.parentId() != null){
            Comment parent = commentRepository.findById(commentRequestDto.parentId())
            .orElseThrow(() -> new EntityNotFoundException("parent comment not null and not found"));
            newComment.setParent(parent);
        }

        Comment saved = commentRepository.save(newComment);
        return commentMapper.toDto(saved);
    }
    public List<CommentDto> getCommentsByPost(UUID postId){
        return commentRepository.findByPostId(postId).stream()
        .map(commentMapper::toDto)
        .toList();
    }







}
