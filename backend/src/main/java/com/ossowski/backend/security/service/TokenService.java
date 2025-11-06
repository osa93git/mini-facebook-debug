package com.ossowski.backend.security.service;

import java.util.List;
import java.util.Optional;

import com.ossowski.backend.exceptions.auth.InvalidTokenException;
import com.ossowski.backend.exceptions.auth.TokenNotFoundException;
import org.springframework.stereotype.Service;

import com.ossowski.backend.security.token.TokenType;
import com.ossowski.backend.security.token.Token;
import com.ossowski.backend.security.repository.TokenRepository;
import com.ossowski.backend.user.model.User;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void saveUserToken(User user, Token token) {
        token.setOwner(user);
        tokenRepository.save(token);
    }

    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllByOwnerAndExpiredFalseAndRevokedFalse(user);

        if (validTokens.isEmpty()) return;

        validTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    public Token findValidRefreshToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .filter(t -> !t.isExpired() && !t.isRevoked() && t.getTokenType() == TokenType.REFRESH)
                .orElseThrow(() -> new TokenNotFoundException(tokenValue));
    }
}
