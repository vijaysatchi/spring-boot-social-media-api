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

    public Jwt generateAccessToken(long userId, Map<String, Object> customClaims){
        return generateToken(userId, customClaims, jwtConfig.getAccessTokenExpiration());

    }

    public Jwt generateRefreshToken(long userId, Map<String, Object> customClaims){
        return generateToken(userId, customClaims, jwtConfig.getRefreshTokenExpiration());
    }

    public Jwt generateToken(long userId, Map<String, Object> customClaims, long expiration){
        var claimsBuilder = Jwts.claims()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000));

        if(customClaims != null) {
            customClaims.forEach(claimsBuilder::add);
        }

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
