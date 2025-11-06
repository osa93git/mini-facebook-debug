package com.ossowski.backend.security.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    /**
     * secret for HS256 (min 32bait; Base64 best)
     */
    private String secret;

    /**
     * Issuer (who create token)
     */
    private String issuer;

    /**
     * Audience (for who token is)
     */
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
