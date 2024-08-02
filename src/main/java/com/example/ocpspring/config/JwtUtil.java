package com.example.ocpspring.config;

import com.example.ocpspring.control.usersControl.ModeratorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private byte[] decodeSecret() {
        return Base64.getDecoder().decode(secret);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        //Long userId = ((CustomUserDetails) userDetails).getUserId(); // Assuming CustomUserDetails contains getUserId method
        //claims.put("id", userId);
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS256, decodeSecret())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Add methods for extracting username and checking if the token is expired
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(decodeSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /*
    public Long extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("id", Long.class);
    }
    */

    public Long extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        Object idClaim = claims.get("id");
        logger.error("Extracted Claims: " + claims);
        logger.error("id Claim: " + idClaim + ", Type: " + (idClaim != null ? idClaim.getClass().getName() : "null"));

        // Check if the idClaim is an instance of Number (could be Integer, Long, etc.)
        if (idClaim instanceof Number) {
            return ((Number) idClaim).longValue(); // Convert Number to Long
        }
        // Check if the idClaim is a String that needs to be parsed
        else if (idClaim instanceof String) {
            try {
                return Long.parseLong((String) idClaim); // Convert String to Long
            } catch (NumberFormatException e) {
                logger.error("Error converting id claim to Long: " + e.getMessage());
            }
        }
        return null; // Return null if idClaim is not of expected types
    }

}
