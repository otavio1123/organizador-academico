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

import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.SemestreService;
import com.organizador.api.service.TokenService;

@RestController
@CrossOrigin(origins = "*")
public class SemestreController {

    private final SemestreService semestreService;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public SemestreController(
            SemestreService semestreService,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {

        this.semestreService = semestreService;
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

    @PostMapping("/Semestres")
    public ResponseEntity<?> criar(
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Semestre semestre) {

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

        try {

            Semestre semestreSalvo =
                    semestreService.cadastrar(
                            semestre,
                            usuario
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(semestreSalvo);

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erro.getMessage()
                    ));

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensagem",
                            erro.getMessage()
                    ));
        }
    }

    @GetMapping("/Semestres")
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

        List<Semestre> semestres =
                semestreService.listar(
                        usuario
                );

        return ResponseEntity.ok(
                semestres
        );
    }

    @GetMapping("/Cursos/{idCurso}/Semestres")
    public ResponseEntity<?> listarPorCurso(
            @PathVariable Integer idCurso,
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

        if (!semestreService
                .cursoPertenceAoUsuario(
                        idCurso,
                        usuario
                )) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Curso não encontrado."
                    ));
        }

        List<Semestre> semestres =
                semestreService.listarPorCurso(
                        idCurso,
                        usuario
                );

        return ResponseEntity.ok(
                semestres
        );
    }

    @PutMapping("/Semestres/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Semestre semestre) {

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

        try {

            Semestre semestreAtualizado =
                    semestreService.atualizar(
                            id,
                            semestre,
                            usuario
                    );

            if (semestreAtualizado == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "mensagem",
                                "Semestre não encontrado."
                        ));
            }

            return ResponseEntity.ok(
                    semestreAtualizado
            );

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erro.getMessage()
                    ));

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensagem",
                            erro.getMessage()
                    ));
        }
    }

    @PutMapping("/Semestres/{id}/ativo")
    public ResponseEntity<?> atualizarAtivo(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody String ativo) {

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

        ativo = ativo
                .replace("\"", "")
                .trim()
                .toUpperCase();

        if (!"S".equals(ativo)
                && !"N".equals(ativo)) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            "Status inválido."
                    ));
        }

        try {

            boolean atualizou =
                    semestreService
                            .atualizarAtivo(
                                    id,
                                    ativo,
                                    usuario
                            );

            if (!atualizou) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "mensagem",
                                "Semestre não encontrado."
                        ));
            }

            return ResponseEntity.ok(
                    Map.of(
                            "mensagem",
                            "Status atualizado com sucesso."
                    )
            );

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensagem",
                            erro.getMessage()
                    ));
        }
    }

    @DeleteMapping("/Semestres/{id}")
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
                semestreService.excluir(
                        id,
                        usuario
                );

        if (!excluiu) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Semestre não encontrado."
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Semestre excluído com sucesso."
                )
        );
    }
}