package com.ossowski.backend.security.auth;

import com.ossowski.backend.security.auth.dto.LoginRequest;
import com.ossowski.backend.security.auth.dto.LoginResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ossowski.backend.security.auth.jwt.JwtService;
import com.ossowski.backend.security.token.TokenType;
import com.ossowski.backend.security.token.Token;
import com.ossowski.backend.security.service.TokenService;
import com.ossowski.backend.user.model.User;
import com.ossowski.backend.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

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

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                //only for frontend testing
//                .secure(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(new LoginResponse(accessToken, null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue("refreshToken") String refreshToken){

        try{
            String subject = jwtService.extractSubject(refreshToken);
        }catch(Exception e){
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Token storedToken = tokenService.findValidRefreshToken(refreshToken);

        if(storedToken == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        User user = storedToken.getOwner();
        String newAccessToken = jwtService.generateAccessToken(user);
         return ResponseEntity.ok(new LoginResponse(newAccessToken, null));
    }
}
