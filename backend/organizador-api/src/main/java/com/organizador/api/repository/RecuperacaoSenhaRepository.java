package com.organizador.api.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.organizador.api.model.RecuperacaoSenha;
import com.organizador.api.model.Usuario;

public interface RecuperacaoSenhaRepository
        extends JpaRepository<RecuperacaoSenha, Long> {

    Optional<RecuperacaoSenha> findFirstByUsuarioEmailOrderByCriadoEmDesc(
            String email
    );

    long countByUsuarioAndCriadoEmAfter(
            Usuario usuario,
            LocalDateTime data
    );

    @Transactional
    void deleteByUsuario(Usuario usuario);
}