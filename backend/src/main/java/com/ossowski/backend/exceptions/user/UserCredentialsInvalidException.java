package com.ossowski.backend.exceptions.user;

import com.ossowski.backend.exceptions.base.UnauthorizedException;

public class UserCredentialsInvalidException extends UnauthorizedException {
    public UserCredentialsInvalidException() {
        super("Invalid username or password.");
    }
}
