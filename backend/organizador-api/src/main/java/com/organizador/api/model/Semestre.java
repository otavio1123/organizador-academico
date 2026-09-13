package com.organizador.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name =  "\"Semestre\"")
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSemestre")
    private Integer idSemestre;
  @Column(name = "idusuario")
    private Integer idusuario;
   @Column(name = "\"Nome\"")
    private String nome;

    @Column(name = "\"DataINICIO\"")
    private LocalDate dataInicio;

    @Column(name = "\"DataFIM\"")
    private LocalDate dataFim;

    @Column(name = "\"Ano\"")
    private Integer ano;

  @Column(name = "ativo")
private String ativo;

    public Semestre() {
    }

    public Semestre(
            Integer idSemestre,
            String nome,
            LocalDate dataInicio,
            LocalDate dataFim,
            Integer ano,
            String ativo
        ) {

        this.idSemestre = idSemestre;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ano = ano;
        this.ativo = ativo;
    }

    public Integer getIdSemestre() {
        return idSemestre;
    }

    public void setIdSemestre(Integer idSemestre) {
        this.idSemestre = idSemestre;
    }
    public Integer getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Integer idusuario) {
        this.idusuario = idusuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getAno() {
    return ano;
}

public void setAno(Integer ano) {
    this.ano = ano;
}

    public String getAtivo() {
    return ativo;
   }

public void setAtivo(String ativo) {
    this.ativo = ativo;
}
}