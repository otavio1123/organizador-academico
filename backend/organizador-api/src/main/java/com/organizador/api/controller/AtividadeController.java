package com.organizador.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.organizador.api.model.Atividade;
import com.organizador.api.model.DocumentoAtividade;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.AtividadeService;
import com.organizador.api.service.TokenService;

@RestController
@CrossOrigin(origins = "*")
public class AtividadeController {

    private final AtividadeService atividadeService;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public AtividadeController(
            AtividadeService atividadeService,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {

        this.atividadeService = atividadeService;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    private Usuario obterUsuario(
            String authorization) {

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            return null;
        }

        try {

            String token =
                    authorization.substring(7);

            String email =
                    tokenService.obterEmail(
                            token
                    );

            return usuarioRepository
                    .findByEmail(
                            email
                    )
                    .orElse(null);

        } catch (RuntimeException erro) {

            return null;
        }
    }

    @GetMapping(
            "/Disciplinas/{idDisciplina}/Atividades"
    )
    public ResponseEntity<?> listar(
            @PathVariable Integer idDisciplina,
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

        List<Atividade> atividades =
                atividadeService
                        .listarPorDisciplina(
                                idDisciplina,
                                usuario
                        );

        return ResponseEntity.ok(
                atividades
        );
    }

    @PostMapping(
            "/Disciplinas/{idDisciplina}/Atividades"
    )
    public ResponseEntity<?> cadastrar(
            @PathVariable Integer idDisciplina,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Atividade atividade) {

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

        try {

            Atividade atividadeSalva =
                    atividadeService
                            .cadastrar(
                                    idDisciplina,
                                    atividade,
                                    usuario
                            );

            return ResponseEntity
                    .status(
                            HttpStatus.CREATED
                    )
                    .body(
                            atividadeSalva
                    );

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );
        }
    }

    @GetMapping(
            "/Atividades/{idAtividade}"
    )
    public ResponseEntity<?> buscar(
            @PathVariable Integer idAtividade,
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

        var atividade =
                atividadeService
                        .buscarDoUsuario(
                                idAtividade,
                                usuario
                        );

        if (atividade.isEmpty()) {

            return ResponseEntity
                    .status(
                            HttpStatus.NOT_FOUND
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Atividade não encontrada."
                            )
                    );
        }

        return ResponseEntity.ok(
                atividade.get()
        );
    }

    @PutMapping(
            "/Atividades/{idAtividade}"
    )
    public ResponseEntity<?> atualizar(
            @PathVariable Integer idAtividade,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Atividade atividade) {

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

        try {

            Atividade atividadeAtualizada =
                    atividadeService
                            .atualizar(
                                    idAtividade,
                                    atividade,
                                    usuario
                            );

            return ResponseEntity.ok(
                    atividadeAtualizada
            );

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );
        }
    }

    @DeleteMapping(
            "/Atividades/{idAtividade}"
    )
    public ResponseEntity<?> excluir(
            @PathVariable Integer idAtividade,
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

        try {

            boolean excluiu =
                    atividadeService
                            .excluir(
                                    idAtividade,
                                    usuario
                            );

            if (!excluiu) {

                return ResponseEntity
                        .status(
                                HttpStatus.NOT_FOUND
                        )
                        .body(
                                Map.of(
                                        "mensagem",
                                        "Atividade não encontrada."
                                )
                        );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "mensagem",
                            "Atividade excluída com sucesso."
                    )
            );

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );
        }
    }

    @GetMapping(
            "/Atividades/{idAtividade}/Documentos"
    )
    public ResponseEntity<?> listarDocumentos(
            @PathVariable Integer idAtividade,
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

        var atividade =
                atividadeService
                        .buscarDoUsuario(
                                idAtividade,
                                usuario
                        );

        if (atividade.isEmpty()) {

            return ResponseEntity
                    .status(
                            HttpStatus.NOT_FOUND
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Atividade não encontrada."
                            )
                    );
        }

        List<DocumentoAtividade> documentos =
                atividadeService
                        .listarDocumentos(
                                idAtividade,
                                usuario
                        );

        return ResponseEntity.ok(
                documentos
        );
    }

    @PostMapping(
            value = "/Atividades/{idAtividade}/Documentos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> adicionarDocumento(
            @PathVariable Integer idAtividade,
            @RequestHeader("Authorization")
            String authorization,
            @RequestParam("arquivo")
            MultipartFile arquivo) {

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

        try {

            DocumentoAtividade documento =
                    atividadeService
                            .adicionarDocumento(
                                    idAtividade,
                                    arquivo,
                                    usuario
                            );

            return ResponseEntity
                    .status(
                            HttpStatus.CREATED
                    )
                    .body(
                            documento
                    );

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );
        }
    }

    @GetMapping(
            "/Atividades/{idAtividade}/Documentos/{idDocumento}/Url"
    )
    public ResponseEntity<?> gerarUrlDocumento(
            @PathVariable Integer idAtividade,
            @PathVariable Integer idDocumento,
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

        try {

            String url =
                    atividadeService
                            .gerarUrlDocumento(
                                    idAtividade,
                                    idDocumento,
                                    usuario
                            );

            return ResponseEntity.ok(
                    Map.of(
                            "url",
                            url
                    )
            );

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .status(
                            HttpStatus.NOT_FOUND
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );
        }
    }

    @DeleteMapping(
            "/Atividades/{idAtividade}/Documentos/{idDocumento}"
    )
    public ResponseEntity<?> excluirDocumento(
            @PathVariable Integer idAtividade,
            @PathVariable Integer idDocumento,
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

        try {

            boolean excluiu =
                    atividadeService
                            .excluirDocumento(
                                    idAtividade,
                                    idDocumento,
                                    usuario
                            );

            if (!excluiu) {

                return ResponseEntity
                        .status(
                                HttpStatus.NOT_FOUND
                        )
                        .body(
                                Map.of(
                                        "mensagem",
                                        "Documento não encontrado."
                                )
                        );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "mensagem",
                            "Documento excluído com sucesso."
                    )
            );

        } catch (IllegalArgumentException erro) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );

        } catch (IllegalStateException erro) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT
                    )
                    .body(
                            Map.of(
                                    "mensagem",
                                    erro.getMessage()
                            )
                    );
        }
    }
}