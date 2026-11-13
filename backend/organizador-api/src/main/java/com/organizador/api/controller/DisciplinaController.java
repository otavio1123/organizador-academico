package com.organizador.api.controller;

import java.math.BigDecimal;
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

import com.organizador.api.model.Disciplina;
import com.organizador.api.model.HorarioDisciplina;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.DisciplinaService;
import com.organizador.api.service.TokenService;

@RestController
@CrossOrigin(origins = "*")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public DisciplinaController(
            DisciplinaService disciplinaService,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {

        this.disciplinaService = disciplinaService;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    private Usuario obterUsuario(String authorization) {

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

    private String validarDisciplina(
            Disciplina disciplina) {

        if (disciplina.getNome() == null
                || disciplina.getNome()
                        .trim()
                        .isEmpty()) {

            return "O nome da disciplina é obrigatório.";
        }

        if (disciplina.getFaltas() != null
                && disciplina.getFaltas() < 0) {

            return "A quantidade de faltas não pode ser negativa.";
        }

        if (!mediaValida(
                disciplina.getMediaMinima())) {

            return "A média mínima deve estar entre 0 e 10.";
        }

        if (!mediaValida(
                disciplina.getMediaFinal())) {

            return "A média final deve estar entre 0 e 10.";
        }
        if (disciplina.getLimiteFaltas() != null
        && disciplina.getLimiteFaltas() <= 0) {

    return "O limite de faltas deve ser maior que zero.";
}

        return null;
    }

    private boolean mediaValida(
            BigDecimal media) {

        if (media == null) {
            return true;
        }

        return media.compareTo(
                BigDecimal.ZERO
        ) >= 0
                && media.compareTo(
                        BigDecimal.TEN
                ) <= 0;
    }

    @GetMapping("/Semestres/{idSemestre}/Disciplinas")
    public ResponseEntity<?> listar(
            @PathVariable Integer idSemestre,
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        if (!disciplinaService
                .semestrePertenceAoUsuario(
                        idSemestre,
                        usuario.getId()
                )) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Semestre não encontrado."
                    ));
        }

        List<Disciplina> disciplinas =
                disciplinaService.listarPorSemestre(
                        idSemestre,
                        usuario
                );

        return ResponseEntity.ok(
                disciplinas
        );
    }

    @PostMapping("/Semestres/{idSemestre}/Disciplinas")
    public ResponseEntity<?> cadastrar(
            @PathVariable Integer idSemestre,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Disciplina disciplina) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        String erro =
                validarDisciplina(
                        disciplina
                );

        if (erro != null) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erro
                    ));
        }

        Disciplina disciplinaSalva =
                disciplinaService.cadastrar(
                        idSemestre,
                        disciplina,
                        usuario
                );

        if (disciplinaSalva == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Semestre não encontrado."
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(disciplinaSalva);
    }

    @GetMapping("/Disciplinas/{id}")
    public ResponseEntity<?> buscar(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        var disciplinaEncontrada =
                disciplinaService.buscarDoUsuario(
                        id,
                        usuario
                );

        if (disciplinaEncontrada.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Disciplina não encontrada."
                    ));
        }

        return ResponseEntity.ok(
                disciplinaEncontrada.get()
        );
    }

    @PutMapping("/Disciplinas/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody Disciplina disciplina) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        String erro =
                validarDisciplina(
                        disciplina
                );

        if (erro != null) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            erro
                    ));
        }

        Disciplina disciplinaAtualizada =
                disciplinaService.atualizar(
                        id,
                        disciplina,
                        usuario
                );

        if (disciplinaAtualizada == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Disciplina não encontrada."
                    ));
        }

        return ResponseEntity.ok(
                disciplinaAtualizada
        );
    }

    @DeleteMapping("/Disciplinas/{id}")
    public ResponseEntity<?> excluir(
            @PathVariable Integer id,
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        boolean excluiu =
                disciplinaService.excluir(
                        id,
                        usuario
                );

        if (!excluiu) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Disciplina não encontrada."
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Disciplina excluída com sucesso."
                )
        );
    }

    @GetMapping("/Disciplinas/{idDisciplina}/Horarios")
    public ResponseEntity<?> listarHorarios(
            @PathVariable Integer idDisciplina,
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        var disciplina =
                disciplinaService.buscarDoUsuario(
                        idDisciplina,
                        usuario
                );

        if (disciplina.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Disciplina não encontrada."
                    ));
        }

        List<HorarioDisciplina> horarios =
                disciplinaService.listarHorarios(
                        idDisciplina,
                        usuario
                );

        return ResponseEntity.ok(
                horarios
        );
    }
@PutMapping("/Disciplinas/{id}/Faltas/Adicionar")
public ResponseEntity<?> adicionarFalta(
        @PathVariable Integer id,
        @RequestHeader("Authorization")
        String authorization) {

    Usuario usuario =
            obterUsuario(authorization);

    if (usuario == null) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "mensagem",
                        "Usuário não encontrado."
                ));
    }

    Disciplina disciplina =
            disciplinaService.adicionarFalta(
                    id,
                    usuario
            );

    if (disciplina == null) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "mensagem",
                        "Disciplina não encontrada."
                ));
    }

    return ResponseEntity.ok(disciplina);
}


@PutMapping("/Disciplinas/{id}/Faltas/Remover")
public ResponseEntity<?> removerFalta(
        @PathVariable Integer id,
        @RequestHeader("Authorization")
        String authorization) {

    Usuario usuario =
            obterUsuario(authorization);

    if (usuario == null) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "mensagem",
                        "Usuário não encontrado."
                ));
    }

    Disciplina disciplina =
            disciplinaService.removerFalta(
                    id,
                    usuario
            );

    if (disciplina == null) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "mensagem",
                        "Não foi possível remover a falta."
                ));
    }

    return ResponseEntity.ok(disciplina);
}
    @PostMapping("/Disciplinas/{idDisciplina}/Horarios")
    public ResponseEntity<?> cadastrarHorario(
            @PathVariable Integer idDisciplina,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody HorarioDisciplina horario) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        if (horario.getDiaSemana() == null
                || horario.getDiaSemana()
                        .trim()
                        .isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            "O dia da semana é obrigatório."
                    ));
        }

        if (horario.getHoraInicio() != null
                && horario.getHoraFim() != null
                && !horario.getHoraFim()
                        .isAfter(
                                horario.getHoraInicio()
                        )) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensagem",
                            "O horário final deve ser posterior ao horário inicial."
                    ));
        }

        HorarioDisciplina horarioSalvo =
                disciplinaService.cadastrarHorario(
                        idDisciplina,
                        horario,
                        usuario
                );

        if (horarioSalvo == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Disciplina não encontrada."
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(horarioSalvo);
    }

    @DeleteMapping("/HorariosDisciplina/{idHorario}")
    public ResponseEntity<?> excluirHorario(
            @PathVariable Integer idHorario,
            @RequestHeader("Authorization")
            String authorization) {

        Usuario usuario =
                obterUsuario(authorization);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "Usuário não encontrado."
                    ));
        }

        boolean excluiu =
                disciplinaService.excluirHorario(
                        idHorario,
                        usuario
                );

        if (!excluiu) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensagem",
                            "Horário não encontrado."
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Horário excluído com sucesso."
                )
        );
    }
}