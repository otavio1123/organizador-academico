package com.organizador.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.organizador.api.model.Curso;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.CursoService;
import com.organizador.api.service.TokenService;

@RestController
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoService cursoService;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public CursoController(
            CursoService cursoService,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {

        this.cursoService = cursoService;
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
                tokenService.obterEmail(
                        token
                );

        return usuarioRepository
                .findByEmail(email)
                .orElse(null);
    }

    private String validarCurso(
            Curso curso) {

        if (curso.getNome() == null
                || curso.getNome()
                        .trim()
                        .isEmpty()) {

            return "O nome do curso é obrigatório.";
        }

        return null;
    }

    @GetMapping("/Cursos")
    public ResponseEntity<?> listar(
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(
                        authorization
                );

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        List<Curso> cursos =
                cursoService.listar(
                        usuario
                );

        return ResponseEntity.ok(
                cursos
        );
    }

    @PostMapping("/Cursos")
    public ResponseEntity<?> cadastrar(
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Curso curso) {

        Usuario usuario =
                obterUsuario(
                        authorization
                );

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        String erro =
                validarCurso(
                        curso
                );

        if (erro != null) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erro
                    ));
        }

        try {

            Curso cursoSalvo =
                    cursoService.cadastrar(
                            curso,
                            usuario
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(cursoSalvo);

        } catch (IllegalArgumentException erroRegra) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erroRegra.getMessage()
                    ));

        } catch (IllegalStateException erroRegra) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensagem",
                            erroRegra.getMessage()
                    ));
        }
    }

    @GetMapping("/Cursos/{id}")
    public ResponseEntity<?> buscar(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(
                        authorization
                );

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        var cursoEncontrado =
                cursoService.buscarDoUsuario(
                        id,
                        usuario
                );

        if (cursoEncontrado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Curso não encontrado."
                    ));
        }

        return ResponseEntity.ok(
                cursoEncontrado.get()
        );
    }

    @PutMapping("/Cursos/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Curso curso) {

        Usuario usuario =
                obterUsuario(
                        authorization
                );

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        String erro =
                validarCurso(
                        curso
                );

        if (erro != null) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erro
                    ));
        }

        try {

            Curso cursoAtualizado =
                    cursoService.atualizar(
                            id,
                            curso,
                            usuario
                    );

            if (cursoAtualizado == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "mensagem",
                                "Curso não encontrado."
                        ));
            }

            return ResponseEntity.ok(
                    cursoAtualizado
            );

        } catch (IllegalArgumentException erroRegra) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erroRegra.getMessage()
                    ));

        } catch (IllegalStateException erroRegra) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensagem",
                            erroRegra.getMessage()
                    ));
        }
    }

    @DeleteMapping("/Cursos/{id}")
    public ResponseEntity<?> excluir(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(
                        authorization
                );

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        boolean excluiu =
                cursoService.excluir(
                        id,
                        usuario
                );

        if (!excluiu) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Curso não encontrado."
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Curso excluído com sucesso."
                )
        );
    }
}