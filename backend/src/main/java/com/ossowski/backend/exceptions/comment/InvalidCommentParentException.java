package com.ossowski.backend.exceptions.comment;

public class InvalidCommentParentException extends RuntimeException {
    public InvalidCommentParentException(String message) {
        super(message);
    }
}
