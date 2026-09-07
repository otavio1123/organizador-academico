package com.organizador.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organizador.api.model.RecuperacaoSenha;

public interface RecuperacaoSenhaRepository
        extends JpaRepository<RecuperacaoSenha, Long> {

    Optional<RecuperacaoSenha> findByTokenHash(String tokenHash);
}