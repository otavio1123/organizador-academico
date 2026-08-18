package com.organizador.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organizador.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByEmail(String email);

}