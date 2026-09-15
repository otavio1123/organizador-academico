package com.organizador.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
   public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {

    if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensagem", "Informe o nome."));
    }

    if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensagem", "Informe o e-mail."));
    }

    usuario.setNome(usuario.getNome().trim());

    String email = usuario.getEmail().trim().toLowerCase();
    usuario.setEmail(email);

    if (usuarioRepository.existsByEmail(email)) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "E-mail já cadastrado."));
    }

    if (usuario.getSenha() == null || usuario.getSenha().length() < 8) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensagem", "A senha deve ter pelo menos 8 caracteres."));
    }

    if (!"S".equalsIgnoreCase(usuario.getAceitouTermos())) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensagem", "É necessário aceitar os Termos de Uso."));
    }

    usuario.setAceitouTermos("S");

    String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
    usuario.setSenha(senhaCriptografada);

    Usuario usuarioSalvo = usuarioRepository.save(usuario);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(usuarioSalvo);
}

    @PostMapping("/usuarios/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        String email = usuario.getEmail().trim().toLowerCase();

        var usuarioEncontrado = usuarioRepository.findByEmail(email);

        if (usuarioEncontrado.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "E-mail ou senha inválidos."
                    ));
        }

        Usuario usuarioBanco = usuarioEncontrado.get();

        boolean senhaCorreta = passwordEncoder.matches(
                usuario.getSenha(),
                usuarioBanco.getSenha()
        );

        if (!senhaCorreta) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "E-mail ou senha inválidos."
                    ));
        }

        return ResponseEntity.ok(usuarioBanco);
    }

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
}