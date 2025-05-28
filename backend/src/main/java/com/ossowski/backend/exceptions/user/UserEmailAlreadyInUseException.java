package com.ossowski.backend.exceptions.user;

import com.ossowski.backend.exceptions.base.BadRequestException;

public class UserEmailAlreadyInUseException extends BadRequestException {
    public UserEmailAlreadyInUseException(String email) {
        super("User with email " + email + " already exists");
    }
}
