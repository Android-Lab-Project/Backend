package com.healthtechbd.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTService {
    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
        return token;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSignInKey()) // Convert jwtSecret to byte array
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            return false;

        } catch (MalformedJwtException ex) {
            return false;

        } catch (ExpiredJwtException ex) {
            return false;

        } catch (UnsupportedJwtException ex) {
            return false;

        } catch (IllegalArgumentException ex) {
            return false;

        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
