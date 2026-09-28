package com.organizador.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.organizador.api.dto.RecuperarSenhaRequest;
import com.organizador.api.dto.RedefinirSenhaRequest;
import com.organizador.api.dto.VerificarCodigoRequest;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.AuthService;
import com.organizador.api.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;

    public AuthController(
            AuthService authService,
            TokenService tokenService,
            UsuarioRepository usuarioRepository,
            LogAuditoriaRepository logAuditoriaRepository) {

        this.authService = authService;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(
            @Valid @RequestBody RecuperarSenhaRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        authService.solicitarRecuperacao(email);

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Se houver uma conta cadastrada com esse e-mail, as instruções de recuperação serão enviadas."
                )
        );
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<?> verificarCodigo(
            @Valid @RequestBody VerificarCodigoRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        boolean codigoValido =
                authService.verificarCodigo(
                        email,
                        request.getCodigo()
                );

        if (!codigoValido) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Código inválido ou expirado."
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Código válido."
                )
        );
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(
            @Valid @RequestBody RedefinirSenhaRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        boolean senhaRedefinida =
                authService.redefinirSenha(
                        email,
                        request.getCodigo(),
                        request.getNovaSenha()
                );

        if (!senhaRedefinida) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Código inválido ou expirado."
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Senha redefinida com sucesso."
                )
        );
    }

    @PostMapping("/verificar-2fa")
    public ResponseEntity<?> verificar2FA(
            @RequestBody Map<String, String> dados) {

        String emailRecebido =
                dados.get("email");

        String codigo =
                dados.get("codigo");

        if (emailRecebido == null
                || codigo == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    "E-mail e código são obrigatórios."
                            )
                    );
        }

        String email =
                emailRecebido
                        .trim()
                        .toLowerCase();

        boolean codigoValido =
                tokenService.verificarCodigo2FA(
                        email,
                        codigo
                );

        if (!codigoValido) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Código inválido ou expirado."
                            )
                    );
        }

        var usuarioEncontrado =
                usuarioRepository.findByEmail(email);

        if (usuarioEncontrado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Usuário não encontrado."
                            )
                    );
        }

        Usuario usuario =
                usuarioEncontrado.get();

        String token =
                tokenService.gerarToken(
                        usuario.getEmail()
                );

        LogAuditoria log =
                new LogAuditoria();

        log.setIdUsuario(
                Long.valueOf(
                        usuario.getId()
                )
        );

        log.setNomeUsuario(
                usuario.getNome()
        );

        log.setAcao(
                "Realizou login"
        );

        logAuditoriaRepository.save(log);

        return ResponseEntity.ok(
                Map.of(
                        "usuario", usuario,
                        "token", token
                )
        );
    }
}