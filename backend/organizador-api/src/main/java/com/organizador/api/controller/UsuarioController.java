package com.organizador.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController

@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

@PostMapping("/usuarios")
public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {

    if (usuarioRepository.existsByEmail(usuario.getEmail())) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "E-mail já cadastrado."));
    }

    String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
    usuario.setSenha(senhaCriptografada);

    Usuario usuarioSalvo = usuarioRepository.save(usuario);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(usuarioSalvo);
}


@PostMapping("/usuarios/login")
public ResponseEntity<?> login(@RequestBody Usuario usuario) {

    System.out.println("===== LOGIN =====");
    System.out.println("E-mail recebido: " + usuario.getEmail());
    System.out.println("Senha recebida: " + usuario.getSenha());

    var usuarioEncontrado = usuarioRepository.findByEmail(usuario.getEmail());

    System.out.println("Usuário encontrado: " + usuarioEncontrado.isPresent());

    if (usuarioEncontrado.isEmpty()) {
        System.out.println("ERRO: E-mail não encontrado.");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensagem", "E-mail ou senha inválidos."));
    }

    Usuario usuarioBanco = usuarioEncontrado.get();

    System.out.println("E-mail no banco: " + usuarioBanco.getEmail());
    System.out.println("Hash da senha no banco: " + usuarioBanco.getSenha());

    boolean senhaCorreta = passwordEncoder.matches(
            usuario.getSenha(),
            usuarioBanco.getSenha()
    );

    System.out.println("Senha confere: " + senhaCorreta);

    if (!senhaCorreta) {
        System.out.println("ERRO: Senha incorreta.");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensagem", "E-mail ou senha inválidos."));
    }

    System.out.println("LOGIN REALIZADO COM SUCESSO!");

    return ResponseEntity.ok(usuarioBanco);
}


    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
}