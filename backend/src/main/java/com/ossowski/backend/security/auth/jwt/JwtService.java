package com.ossowski.backend.security.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

import com.ossowski.backend.user.model.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.ossowski.backend.user.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@Service

public class JwtService {

    private final JwtProperties props;
    private final Key signingKey;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.signingKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private Key getSigningKey() {
        return signingKey;
    }

    public String generateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();
        String subject = user.getId().toString();
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(Role::name).toList());
        claims.put("type", "access");

        return buildToken(
                claims,
                subject,
                props.getAccessTtl().toMillis()
        );
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String subject = user.getId().toString();
        claims.put("type", "refresh");

        return buildToken(
                claims,
                subject,
                props.getRefreshTtl().toMillis()
        );

    }

    public String buildToken(Map<String, Object> claims, String subject, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        Object rawRoles = claims.get("roles");

        if (rawRoles instanceof List<?> rolesList) {
            return rolesList.stream().filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
        return List.of();
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsername(String token) {
        return extractSubject(token);
    }

    public boolean isTokenValid(String token, String expectedSubject) {
        final String actualSubject = extractSubject(token);
        return (actualSubject.equals(expectedSubject) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractClaimAsString(String token, String claimKey) {
        return extractAllClaims(token).get(claimKey, String.class);
    }
}
