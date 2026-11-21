package com.organizador.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;

class TokenServiceTest {

    private TokenService tokenService;

    private static final String SEGREDO =
            "1234567890123456789012345678901234567890123456789012345678901234";

    @BeforeEach
    void configurar() {

        tokenService =
                new TokenService(
                        SEGREDO
                );
    }

    @Test
    void deveGerarToken() {

        String token =
                tokenService.gerarToken(
                        "otavio@email.com"
                );

        assertNotNull(
                token
        );

        assertFalse(
                token.isBlank()
        );
    }

    @Test
    void deveObterEmailDoToken() {

        String token =
                tokenService.gerarToken(
                        "otavio@email.com"
                );

        String email =
                tokenService.obterEmail(
                        token
                );

        assertEquals(
                "otavio@email.com",
                email
        );
    }

    @Test
    void naoDeveAceitarTokenAssinadoComOutraChave() {

        TokenService outroTokenService =
                new TokenService(
                        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789012"
                );

        String token =
                outroTokenService.gerarToken(
                        "outro@email.com"
                );

        assertThrows(
                JwtException.class,
                () ->
                        tokenService.obterEmail(
                                token
                        )
        );
    }

    @Test
    void deveGerarCodigo2FAComSeisDigitos() {

        String codigo =
                tokenService.gerarCodigo2FA(
                        "otavio@email.com"
                );

        assertNotNull(
                codigo
        );

        assertEquals(
                6,
                codigo.length()
        );

        assertTrue(
                codigo.matches("\\d{6}")
        );
    }

    @Test
    void deveValidarCodigo2FACorreto() {

        String codigo =
                tokenService.gerarCodigo2FA(
                        "otavio@email.com"
                );

        boolean resultado =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        codigo
                );

        assertTrue(
                resultado
        );
    }

    @Test
    void deveNormalizarEmailAoValidarCodigo2FA() {

        String codigo =
                tokenService.gerarCodigo2FA(
                        "  OTAVIO@EMAIL.COM  "
                );

        boolean resultado =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        codigo
                );

        assertTrue(
                resultado
        );
    }

    @Test
    void codigo2FADeveSerDeUsoUnico() {

        String codigo =
                tokenService.gerarCodigo2FA(
                        "otavio@email.com"
                );

        boolean primeiraTentativa =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        codigo
                );

        boolean segundaTentativa =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        codigo
                );

        assertTrue(
                primeiraTentativa
        );

        assertFalse(
                segundaTentativa
        );
    }

    @Test
    void deveRetornarFalseQuandoEmailForNull() {

        boolean resultado =
                tokenService.verificarCodigo2FA(
                        null,
                        "123456"
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveRetornarFalseQuandoCodigoForNull() {

        boolean resultado =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        null
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveRetornarFalseQuandoNaoExisteCodigoParaEmail() {

        boolean resultado =
                tokenService.verificarCodigo2FA(
                        "inexistente@email.com",
                        "123456"
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveRetornarFalseParaCodigoIncorreto() {

        String codigoCorreto =
                tokenService.gerarCodigo2FA(
                        "otavio@email.com"
                );

        String codigoErrado =
                codigoCorreto.equals(
                        "000000"
                )
                        ? "999999"
                        : "000000";

        boolean resultado =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        codigoErrado
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void codigoIncorretoNaoDeveInvalidarCodigoCorreto() {

        String codigoCorreto =
                tokenService.gerarCodigo2FA(
                        "otavio@email.com"
                );

        String codigoErrado =
                codigoCorreto.equals(
                        "000000"
                )
                        ? "999999"
                        : "000000";

        boolean tentativaErrada =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        codigoErrado
                );

        boolean tentativaCorreta =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        codigoCorreto
                );

        assertFalse(
                tentativaErrada
        );

        assertTrue(
                tentativaCorreta
        );
    }

    @Test
    void deveRejeitarCodigo2FAExpirado() throws Exception {

        Field campoCodigos =
                TokenService.class
                        .getDeclaredField(
                                "codigos2FA"
                        );

        campoCodigos.setAccessible(
                true
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> codigos =
                (Map<String, Object>)
                        campoCodigos.get(
                                tokenService
                        );

        Class<?> classeCodigo =
                Class.forName(
                        "com.organizador.api.service.TokenService$Codigo2FA"
                );

        Constructor<?> construtor =
                classeCodigo.getDeclaredConstructor(
                        String.class,
                        Instant.class
                );

        construtor.setAccessible(
                true
        );

        Object codigoExpirado =
                construtor.newInstance(
                        "123456",
                        Instant.now()
                                .minusSeconds(10)
                );

        codigos.put(
                "otavio@email.com",
                codigoExpirado
        );

        boolean resultado =
                tokenService.verificarCodigo2FA(
                        "otavio@email.com",
                        "123456"
                );

        assertFalse(
                resultado
        );

        assertFalse(
                codigos.containsKey(
                        "otavio@email.com"
                )
        );
    }
}
