package com.organizador.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.organizador.api.model.Usuario;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.DashboardService;
import com.organizador.api.service.TokenService;

@RestController
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public DashboardController(
            DashboardService dashboardService,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {

        this.dashboardService = dashboardService;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    private Usuario obterUsuario(
            String authorization) {

        String token =
                authorization.replace(
                        "Bearer ",
                        ""
                );

        String email =
                tokenService.obterEmail(token);

        return usuarioRepository
                .findByEmail(email)
                .orElse(null);
    }

    @GetMapping("/Dashboard")
    public ResponseEntity<?> obterDashboard(
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(
                        authorization
                );

        if (usuario == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Usuário não encontrado."
                            )
                    );
        }

        return ResponseEntity.ok(
                dashboardService
                        .obterDashboard(usuario)
        );
    }
}