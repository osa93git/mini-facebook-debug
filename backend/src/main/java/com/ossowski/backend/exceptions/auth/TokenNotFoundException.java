    package com.ossowski.backend.exceptions.auth;

    import com.ossowski.backend.exceptions.base.BadRequestException;

    public class TokenNotFoundException extends BadRequestException {
        public TokenNotFoundException(String token) {
            super("token" + token + "not found");
        }
    }