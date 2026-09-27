package com.organizador.api.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    private final SecretKey chave;

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

    long intervalo =
            Instant.now().getEpochSecond() / 900;

    return gerarCodigo2FA(
            email,
            intervalo
    );
}

private String gerarCodigo2FA(
        String email,
        long intervalo) {

    try {

        Mac mac =
                Mac.getInstance("HmacSHA256");

        mac.init(chave);

        byte[] hash =
                mac.doFinal(
                        ("2FA:" + email + ":" + intervalo)
                                .getBytes(StandardCharsets.UTF_8)
                );

        int numero =
                ((hash[0] & 0xff) << 24)
                | ((hash[1] & 0xff) << 16)
                | ((hash[2] & 0xff) << 8)
                | (hash[3] & 0xff);

        numero =
                Math.abs(numero);

        return String.format(
                "%06d",
                numero % 1000000
        );

    } catch (Exception erro) {

        throw new IllegalStateException(
                "Não foi possível gerar o código 2FA.",
                erro
        );
    }
}

public boolean verificarCodigo2FA(
        String email,
        String codigo) {

    long intervalo =
            Instant.now().getEpochSecond() / 900;

    String codigoAtual =
            gerarCodigo2FA(
                    email,
                    intervalo
            );

    String codigoAnterior =
            gerarCodigo2FA(
                    email,
                    intervalo - 1
            );

    return codigo.equals(codigoAtual)
            || codigo.equals(codigoAnterior);
}
}