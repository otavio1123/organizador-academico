package com.organizador.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.TokenService;

@RestController
@CrossOrigin(origins = "*")
public class LogAuditoriaController {

    private final LogAuditoriaRepository logAuditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public LogAuditoriaController(
            LogAuditoriaRepository logAuditoriaRepository,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {

        this.logAuditoriaRepository = logAuditoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/LogAuditoria")
    public ResponseEntity<?> criar(
            @RequestHeader("Authorization")
            String authorization,

            @RequestBody
            LogAuditoria logAuditoria) {

        String token =
                authorization.replace(
                        "Bearer ",
                        ""
                );

        String email =
                tokenService.obterEmail(token);

        var usuarioEncontrado =
                usuarioRepository.findByEmail(email);

        if (usuarioEncontrado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        Usuario usuario =
                usuarioEncontrado.get();

        logAuditoria.setIdUsuario(
                Long.valueOf(usuario.getId())
        );

        logAuditoria.setNomeUsuario(
                usuario.getNome()
        );

        LogAuditoria logSalvo =
                logAuditoriaRepository.save(
                        logAuditoria
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(logSalvo);
    }
}