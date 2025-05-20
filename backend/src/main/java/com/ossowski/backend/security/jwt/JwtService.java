package com.ossowski.backend.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.ossowski.backend.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {


    // TODO: solve problem how long tokens must be alive 

    private static final String SECRET_KEY = "super-secret-key-whitch-is-long-enough-for-hmac256!";
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24;
    private static final long REFRESH_EXPIRATION_MS = 1000 * 60 * 60 * 24 * 7;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    } 

    // public String generateAccessToken(String subject){
    //     return Jwts.builder()
    //     .setSubject(subject)
    //     .setIssuedAt(new Date())
    //     .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
    //     .signWith(getSigningKey(), SignatureAlgorithm.HS256)
    //     .compact();
    // }

    public String generateAccessToken(User user){
        return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("userId", user.getId().toString())
        .claim("firstName", user.getFirstName())
        .claim("lastName", user.getLastName())
        .claim("photo", user.getProfilePhotoUrl())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }
    public String generateRefreshToken(User user){
        return Jwts.builder()
        .setSubject(user.getEmail())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    public String extractSubject(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public String extractUsername(String token){
        return extractSubject(token);
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
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    public String extractClaimAsString(String token, String claimKey){
        return extractAllClaims(token).get(claimKey, String.class);
    }
}
