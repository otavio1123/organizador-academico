package com.organizador.api.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

import com.organizador.api.model.RecuperacaoSenha;
import com.organizador.api.repository.RecuperacaoSenhaRepository;
import com.organizador.api.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UsuarioRepository usuarioRepository,
            RecuperacaoSenhaRepository recuperacaoSenhaRepository,
            EmailService emailService) {

        this.usuarioRepository = usuarioRepository;
        this.recuperacaoSenhaRepository = recuperacaoSenhaRepository;
        this.emailService = emailService;
    }

    public void solicitarRecuperacao(String email) {

        var usuarioEncontrado = usuarioRepository.findByEmail(email);

        if (usuarioEncontrado.isEmpty()) {
            return;
        }

        var usuario = usuarioEncontrado.get();

        String token = gerarTokenSeguro();
        String tokenHash = gerarHashToken(token);

        LocalDateTime agora = LocalDateTime.now();

        RecuperacaoSenha recuperacao = new RecuperacaoSenha();

        recuperacao.setUsuario(usuario);
        recuperacao.setTokenHash(tokenHash);
        recuperacao.setCriadoEm(agora);
        recuperacao.setExpiraEm(agora.plusMinutes(15));
        recuperacao.setUsadoEm(null);

        recuperacaoSenhaRepository.save(recuperacao);

        String linkRedefinicao =
                "http://127.0.0.1:5500/frontend/redefinir-senha.html?token="
                + token;

        emailService.enviarRecuperacaoSenha(
                usuario.getEmail(),
                linkRedefinicao
        );
    }

    private String gerarTokenSeguro() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String gerarHashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException erro) {

            throw new IllegalStateException(
                    "Não foi possível gerar o hash do token.",
                    erro
            );
        }
    }
}