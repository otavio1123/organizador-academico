package com.organizador.api.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.organizador.api.dto.RecuperarSenhaRequest;
import com.organizador.api.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(
            @Valid @RequestBody RecuperarSenhaRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        authService.solicitarRecuperacao(email);

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Se houver uma conta cadastrada com esse e-mail, as instruções de recuperação serão enviadas."
                )
        );
    }
}