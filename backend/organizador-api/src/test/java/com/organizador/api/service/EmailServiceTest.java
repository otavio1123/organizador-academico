package com.organizador.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

class EmailServiceTest {

    private EmailService emailService;

    private Resend resend;

    @BeforeEach
    void configurar() throws Exception {

        emailService =
                new EmailService(
                        "chave-teste"
                );

        resend =
                mock(
                        Resend.class,
                        RETURNS_DEEP_STUBS
                );

        Field campoResend =
                EmailService.class
                        .getDeclaredField(
                                "resend"
                        );

        campoResend.setAccessible(
                true
        );

        campoResend.set(
                emailService,
                resend
        );
    }

    @Test
    void deveEnviarCodigo2FA() throws Exception {

        emailService.enviarCodigoRecuperacao(
                "otavio@email.com",
                "123456",
                "2FA"
        );

        verify(
                resend.emails()
        ).send(
                any(
                        CreateEmailOptions.class
                )
        );
    }

    @Test
    void deveEnviarCodigoRecuperacaoDeSenha() throws Exception {

        emailService.enviarCodigoRecuperacao(
                "otavio@email.com",
                "654321",
                "RECUPERACAO"
        );

        verify(
                resend.emails()
        ).send(
                any(
                        CreateEmailOptions.class
                )
        );
    }

    @Test
    void deveLancarIllegalStateExceptionQuandoResendFalhar()
            throws Exception {

        ResendException erroResend =
                mock(
                        ResendException.class
                );

        when(
                resend.emails()
                        .send(
                                any(
                                        CreateEmailOptions.class
                                )
                        )
        ).thenThrow(
                erroResend
        );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                emailService
                                        .enviarCodigoRecuperacao(
                                                "otavio@email.com",
                                                "123456",
                                                "2FA"
                                        )
                );

        assertEquals(
                "Não foi possível enviar o e-mail.",
                erro.getMessage()
        );

        assertEquals(
                erroResend,
                erro.getCause()
        );
    }
}