package br.com.marcos.product_order_infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // A chave secreta deve ter pelo menos 32 caracteres para HS256
    private static final String SECRET_KEY = "sua-chave-secreta-muito-longa-e-segura-com-mais-de-32-caracteres";
    private static final long EXPIRATION_TIME = 3600000; // 1h em milissegundos

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username) // Novo método: subject() em vez de setSubject()
                .claim("role", role)
                .issuedAt(new Date()) // Novo método: issuedAt()
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Novo método: expiration()
                .signWith(getSigningKey(), Jwts.SIG.HS256) // Uso correto do SIG
                .compact();
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Novo método: verifyWith()
                .build()
                .parseSignedClaims(token) // Novo método: parseSignedClaims()
                .getPayload(); // Novo método: getPayload() em vez de getBody()
    }
}