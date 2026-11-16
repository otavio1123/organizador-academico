package com.organizador.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organizador.api.model.Curso;

public interface CursoRepository
        extends JpaRepository<Curso, Integer> {

    List<Curso> findByIdUsuario(Integer idUsuario);

    Optional<Curso> findByIdCursoAndIdUsuario(
            Integer idCurso,
            Integer idUsuario
    );
}