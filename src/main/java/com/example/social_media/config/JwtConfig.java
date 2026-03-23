package com.example.social_media.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Data
public class JwtConfig {
    private String secret;
    private long accessTokenExpiration;
    private Set<String> claims;

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
