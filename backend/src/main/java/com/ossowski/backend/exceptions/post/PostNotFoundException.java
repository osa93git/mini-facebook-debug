package com.ossowski.backend.exceptions.post;

import com.ossowski.backend.exceptions.base.NotFoundException;

import java.util.UUID;

public class PostNotFoundException extends NotFoundException {
    public PostNotFoundException(UUID id) {
        super("Post with id " + id + " not found");
    }
}
