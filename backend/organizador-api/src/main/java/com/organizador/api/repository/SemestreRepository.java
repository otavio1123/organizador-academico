package com.organizador.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.organizador.api.model.Semestre;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SemestreRepository extends JpaRepository<Semestre, Integer> {
    java.util.List<Semestre> findByIdusuario(Integer idusuario);
    @Transactional
    @Modifying
    @Query("UPDATE Semestre s SET s.ativo = :ativo WHERE s.idSemestre = :id")
    int atualizarAtivo(@Param("id") Integer id, @Param("ativo") String ativo);
    boolean existsByIdusuarioAndAtivo(Integer idusuario, String ativo);
}