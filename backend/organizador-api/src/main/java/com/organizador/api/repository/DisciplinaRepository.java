package com.organizador.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organizador.api.model.Disciplina;

public interface DisciplinaRepository
        extends JpaRepository<Disciplina, Integer> {

    List<Disciplina> findByIdSemestre(
            Integer idSemestre
    );

    Optional<Disciplina> findByIdDisciplinaAndIdSemestre(
            Integer idDisciplina,
            Integer idSemestre
    );
}