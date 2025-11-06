package com.ossowski.backend.security.auth.jwt;

import java.io.IOException;
import java.util.UUID;

import com.ossowski.backend.security.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.security.SignatureException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.ossowski.backend.security.handler.SecurityAttributes.JWT_ERROR_MSG;
import static com.ossowski.backend.security.handler.SecurityAttributes.JWT_STATUS_CODE;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private static final String BEARER = "Bearer ";

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || request.getServletPath().startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER.length());

        try {

            final Claims claims = jwtService.extractAllClaims(token);
            final String sub = claims.getSubject();

            if (sub != null
                    && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtService.isTokenValid(token, sub)) {

                UUID userId;

                try {
                    userId = UUID.fromString(sub);
                } catch (IllegalArgumentException e) {
                    request.setAttribute(JWT_ERROR_MSG, "invalid_subject_uuid");
                    throw new BadCredentialsException("invalid_subject_uuid", e);
                }

                UserDetails user = customUserDetailsService.loadUserById(userId);

                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            }

            filterChain.doFilter(request, response);

        } catch (MalformedJwtException e) {
            request.setAttribute(JWT_ERROR_MSG, "malformed_jwt");
            request.setAttribute(JWT_STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            throw new BadCredentialsException("malformed_jwt", e);
        } catch (ExpiredJwtException e) {
            request.setAttribute(JWT_ERROR_MSG, "token_expired");
            throw new CredentialsExpiredException("token_expired", e);
        } catch (SignatureException e) {
            request.setAttribute(JWT_ERROR_MSG, "invalid_signature");
            throw new BadCredentialsException("invalid_signature", e);
        } catch (UnsupportedJwtException e) {
            request.setAttribute(JWT_ERROR_MSG, "unsupported_jwt");
            throw new BadCredentialsException("unsupported_jwt", e);
        } catch (IllegalArgumentException e) {
            request.setAttribute(JWT_ERROR_MSG, "empty_or_null_token");
            throw new BadCredentialsException("empty_or_null_token", e);
        } catch (io.jsonwebtoken.JwtException e) {
            request.setAttribute(JWT_ERROR_MSG, "jwt_invalid");
            throw new BadCredentialsException("jwt_invalid", e);
        }
    }
}


