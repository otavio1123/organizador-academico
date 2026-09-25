package com.organizador.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(
            @Value("${resend.api-key}") String apiKey) {

        this.resend = new Resend(apiKey);
    }

    public void enviarCodigoRecuperacao(
            String destinatario,
            String codigo) {

        CreateEmailOptions email =
                CreateEmailOptions.builder()
                        .from(
                                "Organizador Acadêmico "
                                + "<onboarding@resend.dev>"
                        )
                        .to(destinatario)
                        .subject(
                                "Código de recuperação - "
                                + "Organizador Acadêmico"
                        )
                        .text(
                                "Olá!\n\n"
                                + "Recebemos uma solicitação para "
                                + "redefinir a senha da sua conta "
                                + "no Organizador Acadêmico.\n\n"
                                + "Seu código de recuperação é:\n\n"
                                + codigo
                                + "\n\nEste código é válido por "
                                + "15 minutos.\n\n"
                                + "Se você não solicitou a "
                                + "redefinição de senha, ignore "
                                + "este e-mail."
                        )
                        .build();

        try {

            resend.emails().send(email);

        } catch (ResendException erro) {

            throw new IllegalStateException(
                    "Não foi possível enviar o e-mail de recuperação.",
                    erro
            );
        }
    }
}