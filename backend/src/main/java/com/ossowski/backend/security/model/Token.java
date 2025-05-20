package com.ossowski.backend.security.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ossowski.backend.security.jwt.TokenType;
import com.ossowski.backend.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "token", indexes={
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_token_owner", columnList = "owner_id")
})
public class Token {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false, length = 512)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    @Column(nullable = false)
    private boolean expired;
    
    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName= "id")
    private User owner;

}
