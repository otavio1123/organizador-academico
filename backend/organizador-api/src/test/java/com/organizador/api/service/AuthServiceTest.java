package com.organizador.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.RecuperacaoSenha;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.RecuperacaoSenhaRepository;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.AuthService.ResultadoRedefinicaoSenha;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecuperacaoSenhaRepository recuperacaoSenhaRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @Mock
    private Usuario usuario;

    @InjectMocks
    private AuthService authService;
@BeforeEach
void configurarUsuario() {

    lenient()
            .when(usuario.getId())
            .thenReturn(10);

    lenient()
            .when(usuario.getEmail())
            .thenReturn("otavio@email.com");

    lenient()
            .when(usuario.getNome())
            .thenReturn("Otávio");
}
    @Test
    void naoDeveSolicitarRecuperacaoParaEmailInexistente() {

        when(
                usuarioRepository.findByEmail(
                        "inexistente@email.com"
                )
        ).thenReturn(
                Optional.empty()
        );

        authService.solicitarRecuperacao(
                "inexistente@email.com"
        );

        verify(
                recuperacaoSenhaRepository,
                never()
        ).save(
                any()
        );

        verify(
                emailService,
                never()
        ).enviarCodigoRecuperacao(
                anyString(),
                anyString(),
                anyString()
        );

        verify(
                logAuditoriaRepository,
                never()
        ).save(
                any()
        );
    }

    @Test
    void naoDevePermitirMaisDeTresSolicitacoesEmQuinzeMinutos() {

        when(
                usuarioRepository.findByEmail(
                        "otavio@email.com"
                )
        ).thenReturn(
                Optional.of(
                        usuario
                )
        );

        when(
                recuperacaoSenhaRepository
                        .countByUsuarioAndCriadoEmAfter(
                                eq(usuario),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(
                3L
        );

        authService.solicitarRecuperacao(
                "otavio@email.com"
        );

        verify(
                recuperacaoSenhaRepository,
                never()
        ).save(
                any()
        );

        verify(
                emailService,
                never()
        ).enviarCodigoRecuperacao(
                anyString(),
                anyString(),
                anyString()
        );
    }

    @Test
    void deveSolicitarRecuperacaoComSucesso() {

        when(
                usuarioRepository.findByEmail(
                        "otavio@email.com"
                )
        ).thenReturn(
                Optional.of(
                        usuario
                )
        );

        when(
                recuperacaoSenhaRepository
                        .countByUsuarioAndCriadoEmAfter(
                                eq(usuario),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(
                0L
        );

        authService.solicitarRecuperacao(
                "otavio@email.com"
        );

        ArgumentCaptor<RecuperacaoSenha> captorRecuperacao =
                ArgumentCaptor.forClass(
                        RecuperacaoSenha.class
                );

        verify(
                recuperacaoSenhaRepository
        ).save(
                captorRecuperacao.capture()
        );

        RecuperacaoSenha recuperacao =
                captorRecuperacao.getValue();

        assertEquals(
                usuario,
                recuperacao.getUsuario()
        );

        assertNotNull(
                recuperacao.getTokenHash()
        );

        assertEquals(
                64,
                recuperacao.getTokenHash().length()
        );

        assertEquals(
                0,
                recuperacao.getTentativas()
        );

        assertNull(
                recuperacao.getUsadoEm()
        );

        assertNotNull(
                recuperacao.getCriadoEm()
        );

        assertNotNull(
                recuperacao.getExpiraEm()
        );

        verify(
                emailService
        ).enviarCodigoRecuperacao(
                eq("otavio@email.com"),
                anyString(),
                eq("RECUPERACAO")
        );

        ArgumentCaptor<LogAuditoria> captorLog =
                ArgumentCaptor.forClass(
                        LogAuditoria.class
                );

        verify(
                logAuditoriaRepository
        ).save(
                captorLog.capture()
        );

        assertEquals(
                "Solicitou recuperação de senha",
                captorLog.getValue().getAcao()
        );
    }

    @Test
    void verificarCodigoDeveRetornarFalseQuandoNaoExisteRecuperacao() {

        when(
                recuperacaoSenhaRepository
                        .findFirstByUsuarioEmailOrderByCriadoEmDesc(
                                "otavio@email.com"
                        )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                authService.verificarCodigo(
                        "otavio@email.com",
                        "123456"
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void verificarCodigoDeveRetornarFalseQuandoCodigoJaFoiUsado() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setUsadoEm(
                LocalDateTime.now()
        );

        configurarRecuperacao(
                recuperacao
        );

        boolean resultado =
                authService.verificarCodigo(
                        "otavio@email.com",
                        "123456"
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void verificarCodigoDeveRetornarFalseQuandoCodigoExpirou() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setExpiraEm(
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        configurarRecuperacao(
                recuperacao
        );

        boolean resultado =
                authService.verificarCodigo(
                        "otavio@email.com",
                        "123456"
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void verificarCodigoDeveRetornarFalseQuandoLimiteDeTentativasFoiAtingido() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setTentativas(
                5
        );

        configurarRecuperacao(
                recuperacao
        );

        boolean resultado =
                authService.verificarCodigo(
                        "otavio@email.com",
                        "123456"
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void verificarCodigoIncorretoDeveIncrementarTentativas() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setTentativas(
                2
        );

        configurarRecuperacao(
                recuperacao
        );

        boolean resultado =
                authService.verificarCodigo(
                        "otavio@email.com",
                        "999999"
                );

        assertFalse(
                resultado
        );

        assertEquals(
                3,
                recuperacao.getTentativas()
        );

        verify(
                recuperacaoSenhaRepository
        ).save(
                recuperacao
        );
    }

    @Test
    void verificarCodigoIncorretoDeveConsiderarTentativasNullComoZero() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setTentativas(
                null
        );

        configurarRecuperacao(
                recuperacao
        );

        boolean resultado =
                authService.verificarCodigo(
                        "otavio@email.com",
                        "999999"
                );

        assertFalse(
                resultado
        );

        assertEquals(
                1,
                recuperacao.getTentativas()
        );
    }

    @Test
    void verificarCodigoCorretoDeveRetornarTrue() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        configurarRecuperacao(
                recuperacao
        );

        boolean resultado =
                authService.verificarCodigo(
                        "otavio@email.com",
                        "123456"
                );

        assertTrue(
                resultado
        );
    }

    @Test
    void redefinirSenhaDeveRetornarCodigoInvalidoQuandoRecuperacaoNaoExiste() {

        when(
                recuperacaoSenhaRepository
                        .findFirstByUsuarioEmailOrderByCriadoEmDesc(
                                "otavio@email.com"
                        )
        ).thenReturn(
                Optional.empty()
        );

        ResultadoRedefinicaoSenha resultado =
                authService.redefinirSenha(
                        "otavio@email.com",
                        "123456",
                        "NovaSenha123"
                );

        assertEquals(
                ResultadoRedefinicaoSenha.CODIGO_INVALIDO,
                resultado
        );
    }

    @Test
    void redefinirSenhaDeveRetornarCodigoInvalidoQuandoCodigoJaFoiUsado() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setUsadoEm(
                LocalDateTime.now()
        );

        configurarRecuperacao(
                recuperacao
        );

        ResultadoRedefinicaoSenha resultado =
                authService.redefinirSenha(
                        "otavio@email.com",
                        "123456",
                        "NovaSenha123"
                );

        assertEquals(
                ResultadoRedefinicaoSenha.CODIGO_INVALIDO,
                resultado
        );
    }

    @Test
    void redefinirSenhaDeveRetornarCodigoInvalidoQuandoCodigoExpirou() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setExpiraEm(
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        configurarRecuperacao(
                recuperacao
        );

        ResultadoRedefinicaoSenha resultado =
                authService.redefinirSenha(
                        "otavio@email.com",
                        "123456",
                        "NovaSenha123"
                );

        assertEquals(
                ResultadoRedefinicaoSenha.CODIGO_INVALIDO,
                resultado
        );
    }

    @Test
    void redefinirSenhaDeveRetornarCodigoInvalidoQuandoLimiteTentativasAtingido() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setTentativas(
                5
        );

        configurarRecuperacao(
                recuperacao
        );

        ResultadoRedefinicaoSenha resultado =
                authService.redefinirSenha(
                        "otavio@email.com",
                        "123456",
                        "NovaSenha123"
                );

        assertEquals(
                ResultadoRedefinicaoSenha.CODIGO_INVALIDO,
                resultado
        );
    }

    @Test
    void redefinirSenhaComCodigoIncorretoDeveIncrementarTentativas() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        recuperacao.setTentativas(
                1
        );

        configurarRecuperacao(
                recuperacao
        );

        ResultadoRedefinicaoSenha resultado =
                authService.redefinirSenha(
                        "otavio@email.com",
                        "999999",
                        "NovaSenha123"
                );

        assertEquals(
                ResultadoRedefinicaoSenha.CODIGO_INVALIDO,
                resultado
        );

        assertEquals(
                2,
                recuperacao.getTentativas()
        );

        verify(
                recuperacaoSenhaRepository
        ).save(
                recuperacao
        );
    }

    @Test
    void naoDevePermitirNovaSenhaIgualAAtual() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        when(
                usuario.getSenha()
        ).thenReturn(
                encoder.encode(
                        "SenhaAtual123"
                )
        );

        configurarRecuperacao(
                recuperacao
        );

        ResultadoRedefinicaoSenha resultado =
                authService.redefinirSenha(
                        "otavio@email.com",
                        "123456",
                        "SenhaAtual123"
                );

        assertEquals(
                ResultadoRedefinicaoSenha.SENHA_IGUAL_ATUAL,
                resultado
        );

        verify(
                usuarioRepository,
                never()
        ).save(
                usuario
        );

        assertNull(
                recuperacao.getUsadoEm()
        );
    }

    @Test
    void deveRedefinirSenhaComSucesso() {

        RecuperacaoSenha recuperacao =
                criarRecuperacao(
                        "123456"
                );

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        when(
                usuario.getSenha()
        ).thenReturn(
                encoder.encode(
                        "SenhaAntiga123"
                )
        );

        configurarRecuperacao(
                recuperacao
        );

        ResultadoRedefinicaoSenha resultado =
                authService.redefinirSenha(
                        "otavio@email.com",
                        "123456",
                        "NovaSenha123"
                );

        assertEquals(
                ResultadoRedefinicaoSenha.SUCESSO,
                resultado
        );

        ArgumentCaptor<String> captorSenha =
                ArgumentCaptor.forClass(
                        String.class
                );

        verify(
                usuario
        ).setSenha(
                captorSenha.capture()
        );

        String senhaCriptografada =
                captorSenha.getValue();

        assertTrue(
                encoder.matches(
                        "NovaSenha123",
                        senhaCriptografada
                )
        );

        verify(
                usuarioRepository
        ).save(
                usuario
        );

        assertNotNull(
                recuperacao.getUsadoEm()
        );

        verify(
                recuperacaoSenhaRepository
        ).save(
                recuperacao
        );

        ArgumentCaptor<LogAuditoria> captorLog =
                ArgumentCaptor.forClass(
                        LogAuditoria.class
                );

        verify(
                logAuditoriaRepository
        ).save(
                captorLog.capture()
        );

        assertEquals(
                "Redefiniu a senha",
                captorLog.getValue().getAcao()
        );
    }

    private RecuperacaoSenha criarRecuperacao(
            String codigo) {

        RecuperacaoSenha recuperacao =
                new RecuperacaoSenha();

        recuperacao.setUsuario(
                usuario
        );

        recuperacao.setTokenHash(
                gerarHash(
                        codigo
                )
        );

        recuperacao.setCriadoEm(
                LocalDateTime.now()
        );

        recuperacao.setExpiraEm(
                LocalDateTime.now()
                        .plusMinutes(15)
        );

        recuperacao.setTentativas(
                0
        );

        recuperacao.setUsadoEm(
                null
        );

        return recuperacao;
    }

    private void configurarRecuperacao(
            RecuperacaoSenha recuperacao) {

        when(
                recuperacaoSenhaRepository
                        .findFirstByUsuarioEmailOrderByCriadoEmDesc(
                                "otavio@email.com"
                        )
        ).thenReturn(
                Optional.of(
                        recuperacao
                )
        );
    }

    private String gerarHash(
            String codigo) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            codigo.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(
                            hash
                    );

        } catch (Exception erro) {

            throw new RuntimeException(
                    erro
            );
        }
    }
}