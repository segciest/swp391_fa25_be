package org.grp8.swp391.config;

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

    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jp.getExpiration());
        return Jwts.builder().setSubject(username).setIssuedAt(now).setExpiration(expiration).signWith(Keys.hmacShaKeyFor(jp.getSecret().getBytes()), SignatureAlgorithm.HS256).compact();


    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jp.getSecret().getBytes()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean checkValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jp.getSecret().getBytes()).build().parseClaimsJws(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
