package com.example.social_media.services;

import com.example.social_media.config.JwtConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public Jwt generateToken(String email, Map<String, Object> customClaims){
        var claimsBuilder = Jwts.claims()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration() * 1000));

        customClaims.forEach(claimsBuilder::add);

        return new Jwt(
                claimsBuilder.build(),
                jwtConfig.getSecretKey()
        );
    }

    public Jwt parseToken(String token){
        try {
            var claims = Jwts.parser()
                    .verifyWith(jwtConfig.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new Jwt(claims, jwtConfig.getSecretKey());
        }catch(JwtException e){
            return null;
        }
    }
}
