package com.ossowski.backend.exceptions.auth;

public class JwtAuthenticationException extends org.springframework.security.core.AuthenticationException {
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
