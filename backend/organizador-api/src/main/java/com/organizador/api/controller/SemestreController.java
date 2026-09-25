package com.organizador.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.organizador.api.model.Semestre;
import com.organizador.api.repository.SemestreRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins = "*")
public class SemestreController {

    private final SemestreRepository semestreRepository;

    public SemestreController(SemestreRepository semestreRepository) {
        this.semestreRepository = semestreRepository;
    }

   @PostMapping("/Semestres")
    public ResponseEntity<?> criar(@RequestBody Semestre semestre) {

        if (semestre.getIdusuario() == null) {
           return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Usuário não informado.");
}

     if ("S".equals(semestre.getAtivo())
            && semestreRepository.existsByIdusuarioAndAtivo(semestre.getIdusuario(), "S")) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Já existe um semestre ativo para este usuário.");
     }

      Semestre semestreSalvo = semestreRepository.save(semestre);

     return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(semestreSalvo);
     }

     @GetMapping("/Semestres")
     public List<Semestre> listar(@RequestParam Integer idusuario) {
        return semestreRepository.findByIdusuario(idusuario);
     }

     @DeleteMapping("/Semestres/{id}")
     public ResponseEntity<?> excluir(@PathVariable Integer id) {

        if (!semestreRepository.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Semestre não encontrado.");
        }

        semestreRepository.deleteById(id);

        return ResponseEntity.ok("Semestre excluído com sucesso.");
     }

        @PutMapping("/Semestres/{id}")
         public ResponseEntity<?> atualizar(

     @PathVariable Integer id,
     @RequestBody Semestre semestre) {

     if (!semestreRepository.existsById(id)) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Semestre não encontrado.");
     }

     if (semestre.getIdusuario() == null) {
     return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Usuário não informado.");
        }

     if ("S".equals(semestre.getAtivo())
            && semestreRepository.existsByIdusuarioAndAtivo(semestre.getIdusuario(), "S")) {

        Semestre semestreAtual = semestreRepository.findById(id).get();

        if (!"S".equals(semestreAtual.getAtivo())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Já existe um semestre ativo para este usuário.");
        }
     }
     semestre.setIdSemestre(id);
     Semestre semestreAtualizado = semestreRepository.save(semestre);
     return ResponseEntity.ok(semestreAtualizado);
     }
     @PutMapping("/Semestres/{id}/ativo")
     public ResponseEntity<?> atualizarAtivo(
        @PathVariable Integer id,
        @RequestBody String ativo) {

     if (!semestreRepository.existsById(id)) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Semestre não encontrado.");
     }


     if ("S".equals(ativo)) {
        Semestre semestreAtual = semestreRepository.findById(id).get();

        if (!"S".equals(semestreAtual.getAtivo())
                && semestreRepository.existsByIdusuarioAndAtivo(semestreAtual.getIdusuario(), "S")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Já existe um semestre ativo para este usuário.");
        }
    }

    semestreRepository.atualizarAtivo(id, ativo);

    return ResponseEntity.ok("Status atualizado com sucesso.");
}


}