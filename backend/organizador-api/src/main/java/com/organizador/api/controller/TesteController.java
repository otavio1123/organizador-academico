package com.organizador.api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class TesteController {

    @GetMapping("/")
    public String inicio() {
        return "API do Organizador Acadêmico rodando!";
    }

    @GetMapping("/teste")
    public String teste() {
        return "Backend Java funcionando!";
    }
}