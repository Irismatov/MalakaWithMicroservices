package com.malaka.aat.external.security.jwt;

import com.malaka.aat.external.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${app.security.secretKey}")
    private String jwtSecret;

    @Value("${app.security.accessTokenExpirationInHours}")
    private long accessTokenExpirationInHours;


    @Value("${app.security.refreshTokenExpirationInHours}")
    private long refreshTokenExpirationInHours;


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        long expirationInMs = accessTokenExpirationInHours * 3600 * 1000;
        Date expiryDate = new Date(System.currentTimeMillis() + expirationInMs);


        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }


    public String generateRefreshToken(Authentication authentication) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();


        long expirationInMs = refreshTokenExpirationInHours * 3600 * 1000;

        Date expiryDate = new Date(System.currentTimeMillis() + expirationInMs);

        return Jwts.builder()
                .subject(principal.getUsername())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }


    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(authToken);
        return true;
    }

}
