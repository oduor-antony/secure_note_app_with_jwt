package com.jwt.secure_notes_app.services;

import com.jwt.secure_notes_app.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service

public class JwtService {
    private final SecretKey key;
    private final  long expirationMs;
    public JwtService(@Value("${jwt.secret}") String secret,@Value("${jwt.expiration-ms}") long expirationMs){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;

    }

    public String generateToken(String userName, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userName)
                .claim("role",role.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime()+expirationMs))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role",String.class);
    }

    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        try {
            Claims claims=parseClaims(token);
            return claims.getSubject().equals(userDetails.getUsername())&&
                    claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;   // bad signature, malformed, expired, null or empty token
        }

    }



}
