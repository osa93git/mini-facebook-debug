package com.ossowski.backend.security.service;

public interface CurrentUserService {

    /**
     * Return logged user email from SecurityContext
     *
     * @throws IllegalStateException if no user in context
     */
    String getCurrentUserEmail();
}
