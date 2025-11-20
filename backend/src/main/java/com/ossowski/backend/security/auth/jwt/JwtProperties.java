package com.ossowski.backend.security.auth.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Validated
public class JwtProperties {
    /**
     * secret for HS256 (min 32bait; Base64 best)
     */
    @NotBlank
    private String secret;

    /**
     * Issuer (who create token)
     */
    @NotBlank
    private String issuer;

    /**
     * Audience (for who token is)
     */
    @NotBlank
    private String audience;

    /**
     * time skew tollerance
     */
    private Duration clockSkew = Duration.ofSeconds(60);

    /**
     * time to live of access token
     */
    private Duration accessTtl = Duration.ofMinutes(15);

    /**
     * time to live of refresh token
     */
    private Duration refreshTtl = Duration.ofDays(7);
}
