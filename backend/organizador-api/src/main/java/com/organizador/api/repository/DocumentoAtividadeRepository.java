package com.organizador.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organizador.api.model.DocumentoAtividade;

public interface DocumentoAtividadeRepository
        extends JpaRepository<DocumentoAtividade, Integer> {

    List<DocumentoAtividade> findByIdAtividade(
            Integer idAtividade
    );

    Optional<DocumentoAtividade> findByIdDocumentoAndIdAtividade(
            Integer idDocumento,
            Integer idAtividade
    );
}