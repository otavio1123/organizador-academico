package com.organizador.api.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.organizador.api.model.RecuperacaoSenha;
import com.organizador.api.repository.RecuperacaoSenhaRepository;
import com.organizador.api.repository.UsuarioRepository;

@Service
public class AuthService {

    private static final int LIMITE_SOLICITACOES = 3;
    private static final int TEMPO_LIMITE_MINUTOS = 15;
    private static final int TEMPO_EXPIRACAO_CODIGO_MINUTOS = 15;
    private static final int LIMITE_TENTATIVAS_CODIGO = 5;

    private final UsuarioRepository usuarioRepository;
    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

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

        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime inicioLimite =
                agora.minusMinutes(TEMPO_LIMITE_MINUTOS);

        long quantidadeSolicitacoes =
                recuperacaoSenhaRepository
                        .countByUsuarioAndCriadoEmAfter(
                                usuario,
                                inicioLimite
                        );

        if (quantidadeSolicitacoes >= LIMITE_SOLICITACOES) {
            return;
        }

        String codigo = gerarCodigoRecuperacao();
        String codigoHash = gerarHashCodigo(codigo);

        RecuperacaoSenha recuperacao = new RecuperacaoSenha();

        recuperacao.setUsuario(usuario);
        recuperacao.setTokenHash(codigoHash);
        recuperacao.setCriadoEm(agora);
        recuperacao.setExpiraEm(
                agora.plusMinutes(TEMPO_EXPIRACAO_CODIGO_MINUTOS)
        );
        recuperacao.setUsadoEm(null);
        recuperacao.setTentativas(0);

        recuperacaoSenhaRepository.save(recuperacao);

        emailService.enviarCodigoRecuperacao(
                usuario.getEmail(),
                codigo
        );
    }

    @Transactional
    public boolean verificarCodigo(String email, String codigo) {

        String codigoHash = gerarHashCodigo(codigo);

        var recuperacaoEncontrada =
                recuperacaoSenhaRepository
                        .findFirstByUsuarioEmailOrderByCriadoEmDesc(
                                email
                        );

        if (recuperacaoEncontrada.isEmpty()) {
            return false;
        }

        var recuperacao = recuperacaoEncontrada.get();

        if (recuperacao.getUsadoEm() != null) {
            return false;
        }

        if (recuperacao.getExpiraEm().isBefore(LocalDateTime.now())) {
            return false;
        }

        int tentativas = recuperacao.getTentativas() == null
                ? 0
                : recuperacao.getTentativas();

        if (tentativas >= LIMITE_TENTATIVAS_CODIGO) {
            return false;
        }

        if (!recuperacao.getTokenHash().equals(codigoHash)) {

            recuperacao.setTentativas(tentativas + 1);

            recuperacaoSenhaRepository.save(recuperacao);

            return false;
        }

        return true;
    }

    @Transactional
    public boolean redefinirSenha(
            String email,
            String codigo,
            String novaSenha) {

        String codigoHash = gerarHashCodigo(codigo);

        var recuperacaoEncontrada =
                recuperacaoSenhaRepository
                        .findFirstByUsuarioEmailOrderByCriadoEmDesc(
                                email
                        );

        if (recuperacaoEncontrada.isEmpty()) {
            return false;
        }

        var recuperacao = recuperacaoEncontrada.get();

        if (recuperacao.getUsadoEm() != null) {
            return false;
        }

        if (recuperacao.getExpiraEm().isBefore(LocalDateTime.now())) {
            return false;
        }

        int tentativas = recuperacao.getTentativas() == null
                ? 0
                : recuperacao.getTentativas();

        if (tentativas >= LIMITE_TENTATIVAS_CODIGO) {
            return false;
        }

        if (!recuperacao.getTokenHash().equals(codigoHash)) {

            recuperacao.setTentativas(tentativas + 1);

            recuperacaoSenhaRepository.save(recuperacao);

            return false;
        }

        var usuario = recuperacao.getUsuario();

        String senhaCriptografada =
                passwordEncoder.encode(novaSenha);

        usuario.setSenha(senhaCriptografada);

        usuarioRepository.save(usuario);

        recuperacao.setUsadoEm(LocalDateTime.now());

        recuperacaoSenhaRepository.save(recuperacao);

        return true;
    }

    private String gerarCodigoRecuperacao() {

        int numero = secureRandom.nextInt(1000000);

        return String.format("%06d", numero);
    }

    private String gerarHashCodigo(String codigo) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    codigo.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException erro) {

            throw new IllegalStateException(
                    "Não foi possível gerar o hash do código.",
                    erro
            );
        }
    }
}