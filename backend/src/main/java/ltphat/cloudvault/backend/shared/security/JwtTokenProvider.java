package ltphat.cloudvault.backend.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refresh-secret}")
    private String refreshSecretKey;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateAccessToken(String username) {
        return generateToken(new HashMap<>(), username, accessExpiration, getSigningKey(secretKey));
    }

    public String generateRefreshToken(String username) {
        return generateToken(new HashMap<>(), username, refreshExpiration, getSigningKey(refreshSecretKey));
    }

    private String generateToken(Map<String, Object> extraClaims, String subject, long expiration, SecretKey key) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public boolean isAccessTokenValid(String token, String username) {
        return isTokenValid(token, username, getSigningKey(secretKey));
    }

    public boolean isRefreshTokenValid(String token, String username) {
        return isTokenValid(token, username, getSigningKey(refreshSecretKey));
    }

    private boolean isTokenValid(String token, String username, SecretKey key) {
        final String extractedUsername = extractUsername(token, key);
        return (extractedUsername.equals(username)) && !isTokenExpired(token, key);
    }

    public String extractAccessTokenUsername(String token) {
        return extractUsername(token, getSigningKey(secretKey));
    }

    public String extractRefreshTokenUsername(String token) {
        return extractUsername(token, getSigningKey(refreshSecretKey));
    }

    private String extractUsername(String token, SecretKey key) {
        return extractClaim(token, Claims::getSubject, key);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token, SecretKey key) {
        return extractExpiration(token, key).before(new Date());
    }

    private Date extractExpiration(String token, SecretKey key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    private SecretKey getSigningKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractId(String token, boolean isRefresh) {
        SecretKey key = isRefresh ? getSigningKey(refreshSecretKey) : getSigningKey(secretKey);
        return extractAllClaims(token, key).getSubject(); // Using subject as user ID/email
    }
    
    public String extractTokenId(String token, boolean isRefresh) {
        SecretKey key = isRefresh ? getSigningKey(refreshSecretKey) : getSigningKey(secretKey);
        return extractAllClaims(token, key).getId();
    }
}
