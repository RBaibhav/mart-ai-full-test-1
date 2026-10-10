package com.shopsmart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${shopsmart.jwt.secret:shopsmart-super-secret-jwt-key-2026-minimum-256-bits-length-secure-key}") String secret,
            @Value("${shopsmart.jwt.expiration-ms:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UUID customerId, String email, String name, String membershipTier) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(customerId.toString())
                .claim("email", email)
                .claim("name", name)
                .claim("membershipTier", membershipTier)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public CurrentUser parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID id = UUID.fromString(claims.getSubject());
            String email = claims.get("email", String.class);
            String name = claims.get("name", String.class);
            String membershipTier = claims.get("membershipTier", String.class);

            return new CurrentUser(id, name, email, membershipTier);
        } catch (Exception e) {
            return null;
        }
    }
}
