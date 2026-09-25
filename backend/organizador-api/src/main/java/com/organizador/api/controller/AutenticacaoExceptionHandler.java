package com.organizador.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class AutenticacaoExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> tokenExpirado() {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "mensagem",
                        "Sessão expirada. Faça login novamente."
                ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> tokenInvalido() {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "mensagem",
                        "Token inválido."
                ));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> tokenNaoInformado(
            MissingRequestHeaderException erro) {

        if ("Authorization".equals(
                erro.getHeaderName())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Token de autenticação não informado."
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "mensagem",
                        "Cabeçalho obrigatório não informado."
                ));
    }
}
