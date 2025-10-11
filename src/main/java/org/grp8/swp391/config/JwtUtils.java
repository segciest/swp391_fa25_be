package org.grp8.swp391.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
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
        .signWith(Keys.hmacShaKeyFor(jp.getSecret().getBytes()))
        .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims c = parseClaims(token);
            return c == null ? null : c.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            Claims c = parseClaims(token);
            return c == null ? null : (String) c.get("role");
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean checkValidToken(String token) {
        try {
            return parseClaims(token) != null;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            // allow a small clock skew (seconds) to account for minor clock differences
            return Jwts.parserBuilder()
                    .setSigningKey(jp.getSecret().getBytes())
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // token expired - return null and let callers decide
            return null;
        }
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
