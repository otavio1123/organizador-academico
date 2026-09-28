package com.organizador.api.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    private final SecretKey chave;

    private final SecureRandom secureRandom =
            new SecureRandom();

    private final Map<String, Codigo2FA> codigos2FA =
            new ConcurrentHashMap<>();

    public TokenService(
            @Value("${jwt.secret}") String segredo) {

        this.chave = Keys.hmacShaKeyFor(
                segredo.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String gerarToken(String email) {

        Instant agora = Instant.now();

        Instant expiracao =
                agora.plus(2, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(chave)
                .compact();
    }

    public String obterEmail(String token) {

        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String gerarCodigo2FA(String email) {

        String emailNormalizado =
                email.trim().toLowerCase();

        String codigo =
                String.format(
                        "%06d",
                        secureRandom.nextInt(1000000)
                );

        Instant expiracao =
                Instant.now()
                        .plus(15, ChronoUnit.MINUTES);

        codigos2FA.put(
                emailNormalizado,
                new Codigo2FA(
                        codigo,
                        expiracao
                )
        );

        return codigo;
    }

    public boolean verificarCodigo2FA(
            String email,
            String codigo) {

        if (email == null || codigo == null) {
            return false;
        }

        String emailNormalizado =
                email.trim().toLowerCase();

        Codigo2FA registro =
                codigos2FA.get(emailNormalizado);

        if (registro == null) {
            return false;
        }

        if (Instant.now().isAfter(
                registro.expiracao())) {

            codigos2FA.remove(emailNormalizado);

            return false;
        }

        if (!registro.codigo().equals(codigo)) {
            return false;
        }

        codigos2FA.remove(emailNormalizado);

        return true;
    }

    private record Codigo2FA(
            String codigo,
            Instant expiracao) {
    }
}