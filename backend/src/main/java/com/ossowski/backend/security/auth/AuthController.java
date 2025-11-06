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

        //checking if such a login and passwod combination exists
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        //creating access and refresh token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        //revoking all tokens
        tokenService.revokeAllUserTokens(user);

        //creating refresh token
        tokenService.saveUserToken(user, Token.builder()
            .token(refreshToken)
            .tokenType(TokenType.REFRESH)
            .expired(false)
            .revoked(false)
            .owner(user)
            .build()
        );

        //creating cookie with refresh token for browser
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                //only for frontend testing
//                .secure(true)
                .secure(false)
                .path("/auth/refresh")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(new LoginResponse(accessToken, null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue("refreshToken") String refreshToken){

        //extracting email from refreshrtoken
        try{
            String subject = jwtService.extractSubject(refreshToken);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //finding valid refresh token
        Token storedToken = tokenService.findValidRefreshToken(refreshToken);

        //response with exception if token doesn't exist or is expired
        if(storedToken == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        //generating new access token
        User user = storedToken.getOwner();
        String newAccessToken = jwtService.generateAccessToken(user);
         return ResponseEntity.ok(new LoginResponse(newAccessToken, null));
    }
}
