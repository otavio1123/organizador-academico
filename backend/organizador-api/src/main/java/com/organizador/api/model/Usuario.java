package com.organizador.api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @Column(name = "aceitou_termos", length = 1)
    private String aceitouTermos;

    @Column(name = "data_aceite_termos")
    private LocalDateTime dataAceiteTermos;

    @Column(name = "versao_termos")
    private String versaoTermos;

    public Usuario() {
    }

    public Usuario(
            Integer id,
            String nome,
            String email,
            String senha,
            String aceitouTermos,
            LocalDateTime dataAceiteTermos,
            String versaoTermos) {

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.aceitouTermos = aceitouTermos;
        this.dataAceiteTermos = dataAceiteTermos;
        this.versaoTermos = versaoTermos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getAceitouTermos() {
        return aceitouTermos;
    }

    public void setAceitouTermos(String aceitouTermos) {
        this.aceitouTermos = aceitouTermos;
    }

    public LocalDateTime getDataAceiteTermos() {
        return dataAceiteTermos;
    }

    public void setDataAceiteTermos(LocalDateTime dataAceiteTermos) {
        this.dataAceiteTermos = dataAceiteTermos;
    }

    public String getVersaoTermos() {
        return versaoTermos;
    }

    public void setVersaoTermos(String versaoTermos) {
        this.versaoTermos = versaoTermos;
    }
}