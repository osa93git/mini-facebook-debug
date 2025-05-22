package com.ossowski.backend.comment;


import com.ossowski.backend.comment.dto.CommentDto;
import com.ossowski.backend.comment.dto.CommentRequestDto;
import com.ossowski.backend.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal User user){
                CommentDto created = commentService.addComment(dto, user);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable UUID postId){
        List<CommentDto> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }
}
