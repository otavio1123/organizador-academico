package com.organizador.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.UsuarioRepository;

@RestController
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

@PostMapping("/usuarios")
public Usuario cadastrar(@RequestBody Usuario usuario) {
    String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
    usuario.setSenha(senhaCriptografada);

    return usuarioRepository.save(usuario);
}

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
}