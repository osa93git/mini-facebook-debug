package com.ossowski.backend.exceptions.comment;

public class CommentEmptyException extends RuntimeException {
    public CommentEmptyException(String message) {
        super(message);
    }
    public CommentEmptyException() {
        super("Empty comment");
    }
}
