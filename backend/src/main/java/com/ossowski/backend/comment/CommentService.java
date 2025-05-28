package com.ossowski.backend.comment;

import com.ossowski.backend.comment.dto.CommentDto;
import com.ossowski.backend.comment.dto.CommentMapper;
import com.ossowski.backend.comment.dto.CommentRequestDto;
import com.ossowski.backend.comment.model.Comment;
import com.ossowski.backend.exceptions.comment.CommentEmptyException;
import com.ossowski.backend.exceptions.comment.CommentNotFoundException;
import com.ossowski.backend.exceptions.comment.InvalidCommentParentException;
import com.ossowski.backend.exceptions.post.PostNotFoundException;
import com.ossowski.backend.post.model.Post;
import com.ossowski.backend.post.PostRepository;

import com.ossowski.backend.user.model.User;
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
        //checking if comment content and commentRequest content is not null
        if(commentRequestDto.content() == null || commentRequestDto.content().isBlank()){
            throw new CommentEmptyException("Comment content or CommentRequest content is null or empty");
        }

        //finding post to be commented using commentRequest
        Post commentedPost = postRepository.findById(commentRequestDto.postId())
        .orElseThrow(() -> new PostNotFoundException(commentRequestDto.postId()));

        //creating new comment
        Comment newComment = new Comment();
        newComment.setAuthor(author);
        newComment.setPost(commentedPost);
        newComment.setContent(commentRequestDto.content());

        //checking if comment has parent
        if(commentRequestDto.parentId() != null){

            //finding comment parent if present
            Comment commentParent = commentRepository.findById(commentRequestDto.parentId())
            .orElseThrow(() -> new CommentNotFoundException(commentRequestDto.parentId()));

            //consistency comments tree validation
            //if comment parent's id not equals post parent id
            if(!commentParent.getPost().getId().equals(commentedPost.getId())){
                throw new InvalidCommentParentException("Parent comment does not belong to the same post");
            }

            newComment.setParent(commentParent);
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
