package com.example.social_media.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.util.Date;

public class Jwt{
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey secretKey){
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public String getSubject(){
        return claims.getSubject();
    }

    public Long getUserId(){
        return Long.valueOf(claims.getId());
    }

    public boolean isExpired (){
        return claims.getExpiration().after(new Date());
    }

    public String getClaim(String claim) {
        return claims.containsKey(claim) ? claims.get(claim).toString() : null;
    }

    @Override
    public String toString() {
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }
}
