package com.Spring.MindScribe.utils;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
    public static final long JWT_EXPIRATION = 7200000;
    public  static final String JWT_SECRET = "33e4e06e-87c7-11ed-a1eb-0242ac120002";
    public static final String KEY = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes());

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(KEY.getBytes());
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        String email = (String) authentication.getDetails();
        
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        String token = Jwts.builder()
                .setSubject(username)
                .claim("email", email)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(
                    getSigningKey(),
                    SignatureAlgorithm.HS256
                )
                .compact();

        System.out.println("New token:");
        System.out.println(token);

        return token;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect", ex);
        }
    }
}
