package com.logopeda.shared.security;

import com.logopeda.shared.config.AppProperties;
import com.logopeda.shared.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/**
 * Issues and validates signed JWTs for V1 authentication. The token carries the
 * user id (subject) plus the clinic id, email and role as claims, which is all
 * the {@link TenantContext} needs to enforce isolation.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(AppProperties properties) {
        String secret = properties.getSecurity().getJwt().getSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = properties.getSecurity().getJwt().getExpirationMinutes();
    }

    public String generateToken(String userId, String clinicId, String email, Role role) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(userId)
                .claim("clinicId", clinicId)
                .claim("email", email)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public AuthenticatedUser parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new AuthenticatedUser(
                    claims.getSubject(),
                    claims.get("clinicId", String.class),
                    claims.get("email", String.class),
                    Role.valueOf(claims.get("role", String.class)),
                    true);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }
}
