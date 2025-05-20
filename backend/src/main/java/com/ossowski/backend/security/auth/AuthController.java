package com.ossowski.backend.security.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ossowski.backend.security.jwt.JwtService;
import com.ossowski.backend.security.jwt.TokenType;
import com.ossowski.backend.security.model.Token;
import com.ossowski.backend.security.service.TokenService;
import com.ossowski.backend.user.User;
import com.ossowski.backend.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,  UserRepository userRepository, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, Token.builder()
            .token(refreshToken)
            .tokenType(TokenType.REFRESH)
            .expired(false)
            .revoked(false)
            .owner(user)
            .build());

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponse(accessToken, null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue("refreshToken") String refreshToken){
        
        Token storedToken = tokenService.findValidRefreshToken(refreshToken);
        if(storedToken == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        User user = storedToken.getOwner();
        String newAccessToken = jwtService.generateAccessToken(user);
         return ResponseEntity.ok(new LoginResponse(newAccessToken, null));
    }
}
