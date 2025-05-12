package com.ossowski.backend.auth;

public record AuthRequest(
    String email,
    String password
){}
