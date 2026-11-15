package com.example.mioptica.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService() {
        // Genera automáticamente una clave secreta segura de 256 bits
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // Genera un token JWT para un usuario autenticado
    public String generateToken(Authentication authentication) {
        // Roles reales (ROLE_xxx), sin FACTOR_PASSWORD
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .toList();

        // Construir el token JWT
        return Jwts.builder()
                .subject(authentication.getName())  // Email del usuario
                .issuer("gestion-centro-api")       // Quién emite el token
                .issuedAt(new Date())               // Cuándo se creó
                .expiration(new Date(
                        System.currentTimeMillis() + 86400000  // Expira en 24h
                ))
                .claim("roles", roles)              // Lista de roles
                .signWith(secretKey)                // Firmar con clave secreta
                .compact();                         // Generar String del token
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}