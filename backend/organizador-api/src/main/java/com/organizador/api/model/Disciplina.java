package com.organizador.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Disciplina\"")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"IDDisciplina\"")
    private Integer idDisciplina;

    @Column(name = "\"IDSemestre\"")
    private Integer idSemestre;

    @Column(name = "\"Nome\"")
    private String nome;

    @Column(name = "\"NomeProfessor\"")
    private String nomeProfessor;

    @Column(name = "\"Faltas\"")
    private Integer faltas;

    @Column(name = "\"MediaMinima\"")
    private BigDecimal mediaMinima;

    @Column(name = "\"MediaFinal\"")
    private BigDecimal mediaFinal;

    public Disciplina() {
    }

    public Disciplina(
            Integer idDisciplina,
            Integer idSemestre,
            String nome,
            String nomeProfessor,
            Integer faltas,
            BigDecimal mediaMinima,
            BigDecimal mediaFinal
    ) {
        this.idDisciplina = idDisciplina;
        this.idSemestre = idSemestre;
        this.nome = nome;
        this.nomeProfessor = nomeProfessor;
        this.faltas = faltas;
        this.mediaMinima = mediaMinima;
        this.mediaFinal = mediaFinal;
    }

    public Integer getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(Integer idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public Integer getIdSemestre() {
        return idSemestre;
    }

    public void setIdSemestre(Integer idSemestre) {
        this.idSemestre = idSemestre;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    public Integer getFaltas() {
        return faltas;
    }

    public void setFaltas(Integer faltas) {
        this.faltas = faltas;
    }

    public BigDecimal getMediaMinima() {
        return mediaMinima;
    }

    public void setMediaMinima(BigDecimal mediaMinima) {
        this.mediaMinima = mediaMinima;
    }

    public BigDecimal getMediaFinal() {
        return mediaFinal;
    }

    public void setMediaFinal(BigDecimal mediaFinal) {
        this.mediaFinal = mediaFinal;
    }
}