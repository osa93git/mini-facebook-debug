package com.ossowski.backend.security.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ossowski.backend.security.token.Token;
import com.ossowski.backend.user.model.User;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    
    List<Token> findAllByOwnerAndExpiredFalseAndRevokedFalse(User owner);

    Optional<Token> findByToken(String token);

}
