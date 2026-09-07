package com.organizador.api.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarRecuperacaoSenha(String destinatario, String linkRedefinicao) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(destinatario);
        mensagem.setSubject("Redefinição de senha - Organizador Acadêmico");

        mensagem.setText(
                "Olá!\n\n"
                + "Recebemos uma solicitação para redefinir a senha da sua conta "
                + "no Organizador Acadêmico.\n\n"
                + "Para criar uma nova senha, acesse o link abaixo:\n\n"
                + linkRedefinicao
                + "\n\nEste link é válido por 15 minutos.\n\n"
                + "Se você não solicitou a redefinição de senha, ignore este e-mail."
        );

        mailSender.send(mensagem);
    }
}