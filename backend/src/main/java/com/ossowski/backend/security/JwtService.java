package com.ossowski.backend.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "super-secret-key-whitch-is-long-enough-for-hmac256!";
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    } 

    public String generateToken(String subject){
        return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }
    public String extractSubject(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public boolean isTokenValid(String token, String expectedSubject){
        final String actualSubject = extractSubject(token);
        return (actualSubject.equals(expectedSubject) && !isTokenExpired(token));
    }
    public boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

        return claimsResolver.apply(claims);
    }
    
}
