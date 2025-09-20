package org.grp8.swp391.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Autowired
    private JwtProprties jp;

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jp.getExpiration());

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, jp.getSecret().getBytes())
                .compact();
    }

    // Lấy username từ token
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // Lấy role từ token
    public String getRoleFromToken(String token) {
        return (String) parseClaims(token).get("role");
    }

    // Kiểm tra token
    public boolean checkValidToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Parse token -> Claims
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jp.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
