package com.organizador.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organizador.api.model.Atividade;

public interface AtividadeRepository
        extends JpaRepository<Atividade, Integer> {

    List<Atividade> findByIdDisciplina(
            Integer idDisciplina
    );

    Optional<Atividade>
            findByIdAtividadeAndIdDisciplina(
                    Integer idAtividade,
                    Integer idDisciplina
            );

    List<Atividade> findByIdDisciplinaIn(
            List<Integer> idsDisciplinas
    );
}