package com.ossowski.backend.exceptions.post;

public class PostEmptyTextAndUrlException extends RuntimeException {
    public PostEmptyTextAndUrlException() {

        super("Post must contain at least text or media URL.");
    }
}
