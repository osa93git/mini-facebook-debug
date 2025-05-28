package com.ossowski.backend.exceptions.auth;

import com.ossowski.backend.exceptions.base.BadRequestException;

public class InvalidTokenException extends BadRequestException {
    public InvalidTokenException() {
        super("invalid or expired token");
    }
}
