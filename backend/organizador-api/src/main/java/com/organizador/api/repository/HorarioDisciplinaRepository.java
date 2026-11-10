package com.organizador.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organizador.api.model.HorarioDisciplina;

public interface HorarioDisciplinaRepository
        extends JpaRepository<HorarioDisciplina, Integer> {

    List<HorarioDisciplina> findByIdDisciplina(
            Integer idDisciplina
    );

    void deleteByIdDisciplina(
            Integer idDisciplina
    );
}