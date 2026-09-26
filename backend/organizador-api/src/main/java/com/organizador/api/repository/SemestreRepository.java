package com.organizador.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.organizador.api.model.Semestre;

public interface SemestreRepository
        extends JpaRepository<Semestre, Integer> {

    List<Semestre> findByIdusuario(Integer idusuario);

    Optional<Semestre> findByIdSemestreAndIdusuario(
            Integer idSemestre,
            Integer idusuario
    );

    boolean existsByIdusuarioAndAtivo(
            Integer idusuario,
            String ativo
    );

    @Transactional
    void deleteByIdusuario(Integer idusuario);

    @Transactional
    @Modifying
    @Query(
        "UPDATE Semestre s "
        + "SET s.ativo = :ativo "
        + "WHERE s.idSemestre = :id"
    )
    int atualizarAtivo(
            @Param("id") Integer id,
            @Param("ativo") String ativo
    );
}