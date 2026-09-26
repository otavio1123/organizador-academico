package com.organizador.api.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.organizador.api.dto.AlterarSenhaRequest;
import com.organizador.api.dto.ExcluirContaRequest;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.RecuperacaoSenhaRepository;
import com.organizador.api.repository.SemestreRepository;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.TokenService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final SemestreRepository semestreRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            TokenService tokenService,
            LogAuditoriaRepository logAuditoriaRepository,
            RecuperacaoSenhaRepository recuperacaoSenhaRepository,
            SemestreRepository semestreRepository) {

        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.logAuditoriaRepository = logAuditoriaRepository;
        this.recuperacaoSenhaRepository = recuperacaoSenhaRepository;
        this.semestreRepository = semestreRepository;
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> cadastrar(
            @RequestBody Usuario usuario) {

        if (usuario.getNome() == null
                || usuario.getNome().trim().isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "mensagem",
                            "Informe o nome."
                    ));
        }

        if (usuario.getEmail() == null
                || usuario.getEmail().trim().isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "mensagem",
                            "Informe o e-mail."
                    ));
        }

        usuario.setNome(
                usuario.getNome().trim()
        );

        String email =
                usuario.getEmail()
                        .trim()
                        .toLowerCase();

        usuario.setEmail(email);

        if (usuarioRepository.existsByEmail(email)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensagem",
                            "E-mail já cadastrado."
                    ));
        }

        if (usuario.getSenha() == null
                || usuario.getSenha().length() < 8) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "mensagem",
                            "A senha deve ter pelo menos 8 caracteres."
                    ));
        }

        if (!"S".equalsIgnoreCase(
                usuario.getAceitouTermos())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "mensagem",
                            "É necessário aceitar os Termos de Uso."
                    ));
        }

        usuario.setAceitouTermos("S");

        usuario.setDataAceiteTermos(
                LocalDateTime.now()
        );

        usuario.setVersaoTermos("1.1");

        String senhaCriptografada =
                passwordEncoder.encode(
                        usuario.getSenha()
                );

        usuario.setSenha(
                senhaCriptografada
        );

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        LogAuditoria log =
                new LogAuditoria();

        log.setIdUsuario(
                Long.valueOf(
                        usuarioSalvo.getId()
                )
        );

        log.setNomeUsuario(
                usuarioSalvo.getNome()
        );

        log.setAcao(
                "Realizou cadastro"
        );

        logAuditoriaRepository.save(log);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioSalvo);
    }

    @PostMapping("/usuarios/login")
    public ResponseEntity<?> login(
            @RequestBody Usuario usuario) {

        String email =
                usuario.getEmail()
                        .trim()
                        .toLowerCase();

        var usuarioEncontrado =
                usuarioRepository.findByEmail(email);

        if (usuarioEncontrado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "E-mail ou senha inválidos."
                    ));
        }

        Usuario usuarioBanco =
                usuarioEncontrado.get();

        boolean senhaCorreta =
                passwordEncoder.matches(
                        usuario.getSenha(),
                        usuarioBanco.getSenha()
                );

        if (!senhaCorreta) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "E-mail ou senha inválidos."
                    ));
        }

        String token =
                tokenService.gerarToken(
                        usuarioBanco.getEmail()
                );

        LogAuditoria log =
                new LogAuditoria();

        log.setIdUsuario(
                Long.valueOf(
                        usuarioBanco.getId()
                )
        );

        log.setNomeUsuario(
                usuarioBanco.getNome()
        );

        log.setAcao(
                "Realizou login"
        );

        logAuditoriaRepository.save(log);

        return ResponseEntity.ok(
                Map.of(
                        "usuario", usuarioBanco,
                        "token", token
                )
        );
    }

    @PutMapping("/usuarios/senha")
    public ResponseEntity<?> alterarSenha(
            @RequestHeader("Authorization")
            String authorization,

            @Valid
            @RequestBody
            AlterarSenhaRequest request) {

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

        boolean senhaCorreta =
                passwordEncoder.matches(
                        request.getSenhaAtual(),
                        usuario.getSenha()
                );

        if (!senhaCorreta) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Senha atual incorreta."
                    ));
        }

        String novaSenhaCriptografada =
                passwordEncoder.encode(
                        request.getNovaSenha()
                );

        usuario.setSenha(
                novaSenhaCriptografada
        );

        usuarioRepository.save(usuario);

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
                "Alterou a senha"
        );

        logAuditoriaRepository.save(log);

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Senha alterada com sucesso."
                )
        );
    }

    @DeleteMapping("/usuarios")
    public ResponseEntity<?> excluirConta(
            @RequestHeader("Authorization")
            String authorization,

            @Valid
            @RequestBody
            ExcluirContaRequest request) {

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

        boolean senhaCorreta =
                passwordEncoder.matches(
                        request.getSenha(),
                        usuario.getSenha()
                );

        if (!senhaCorreta) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Senha incorreta."
                    ));
        }

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
                "Solicitou exclusão da conta"
        );

        logAuditoriaRepository.save(log);

        recuperacaoSenhaRepository.deleteByUsuario(usuario);

        semestreRepository.deleteByIdusuario(
                usuario.getId()
        );

        usuarioRepository.delete(usuario);

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Conta excluída com sucesso."
                )
        );
    }
}