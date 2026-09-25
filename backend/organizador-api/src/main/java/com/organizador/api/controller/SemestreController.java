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
import com.organizador.api.repository.SemestreRepository;
import com.organizador.api.repository.UsuarioRepository;
import com.organizador.api.service.TokenService;

@RestController
@CrossOrigin(origins = "*")
public class SemestreController {

    private final SemestreRepository semestreRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public SemestreController(
            SemestreRepository semestreRepository,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {

        this.semestreRepository = semestreRepository;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/Semestres")
    public ResponseEntity<?> criar(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Semestre semestre) {

        String token =
                authorization.replace("Bearer ", "");

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

        semestre.setIdusuario(
                usuario.getId()
        );

        if ("S".equals(semestre.getAtivo())
                && semestreRepository.existsByIdusuarioAndAtivo(
                        usuario.getId(),
                        "S")) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                        "Já existe um semestre ativo para este usuário."
                    );
        }

        Semestre semestreSalvo =
                semestreRepository.save(semestre);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(semestreSalvo);
    }

    @GetMapping("/Semestres")
    public ResponseEntity<?> listar(
            @RequestHeader("Authorization") String authorization) {

        String token =
                authorization.replace("Bearer ", "");

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

        List<Semestre> semestres =
                semestreRepository.findByIdusuario(
                        usuario.getId()
                );

        return ResponseEntity.ok(semestres);
    }

    @DeleteMapping("/Semestres/{id}")
    public ResponseEntity<?> excluir(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization) {

        String token =
                authorization.replace("Bearer ", "");

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

        var semestreEncontrado =
                semestreRepository.findByIdSemestreAndIdusuario(
                        id,
                        usuario.getId()
                );

        if (semestreEncontrado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Semestre não encontrado.");
        }

        semestreRepository.delete(
                semestreEncontrado.get()
        );

        return ResponseEntity.ok(
                "Semestre excluído com sucesso."
        );
    }

    @PutMapping("/Semestres/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody Semestre semestre) {

        String token =
                authorization.replace("Bearer ", "");

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

        var semestreEncontrado =
                semestreRepository.findByIdSemestreAndIdusuario(
                        id,
                        usuario.getId()
                );

        if (semestreEncontrado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Semestre não encontrado.");
        }

        Semestre semestreAtual =
                semestreEncontrado.get();

        if ("S".equals(semestre.getAtivo())
                && semestreRepository.existsByIdusuarioAndAtivo(
                        usuario.getId(),
                        "S")) {

            if (!"S".equals(
                    semestreAtual.getAtivo())) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(
                            "Já existe um semestre ativo para este usuário."
                        );
            }
        }

        semestre.setIdSemestre(id);
        semestre.setIdusuario(
                usuario.getId()
        );

        Semestre semestreAtualizado =
                semestreRepository.save(semestre);

        return ResponseEntity.ok(
                semestreAtualizado
        );
    }

    @PutMapping("/Semestres/{id}/ativo")
    public ResponseEntity<?> atualizarAtivo(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody String ativo) {

        String token =
                authorization.replace("Bearer ", "");

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

        var semestreEncontrado =
                semestreRepository.findByIdSemestreAndIdusuario(
                        id,
                        usuario.getId()
                );

        if (semestreEncontrado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Semestre não encontrado.");
        }

        Semestre semestreAtual =
                semestreEncontrado.get();

        if ("S".equals(ativo)
                && !"S".equals(
                        semestreAtual.getAtivo())
                && semestreRepository.existsByIdusuarioAndAtivo(
                        usuario.getId(),
                        "S")) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                        "Já existe um semestre ativo para este usuário."
                    );
        }

        semestreRepository.atualizarAtivo(
                id,
                ativo
        );

        return ResponseEntity.ok(
                "Status atualizado com sucesso."
        );
    }
}