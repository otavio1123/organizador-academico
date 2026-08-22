package com.organizador.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.organizador.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);

}