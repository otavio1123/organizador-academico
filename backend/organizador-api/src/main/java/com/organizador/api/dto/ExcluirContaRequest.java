package com.organizador.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ExcluirContaRequest {

    @NotBlank
    private String senha;

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}