package com.ossowski.backend.exceptions.comment;

import com.ossowski.backend.exceptions.base.NotFoundException;

import java.util.UUID;

public class CommentNotFoundException extends NotFoundException {
    public CommentNotFoundException(UUID id) {
        super("Comment with id " + id + " not found");
    }
}
