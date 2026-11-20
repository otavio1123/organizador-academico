package com.organizador.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "atividades")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idatividade")
    private Integer idAtividade;

    @Column(name = "nome")
    private String nome;

    @Column(name = "iddisciplina")
    private Integer idDisciplina;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "datadeentrega")
    private LocalDate dataDeEntrega;

    @Column(name = "status")
    private String status;

    @Column(name = "notamaxima")
    private BigDecimal notaMaxima;

    @Column(name = "notaobtida")
    private BigDecimal notaObtida;

    @Column(name = "peso")
    private BigDecimal peso;

    @Column(name = "prioridade")
    private String prioridade;

    @Column(name = "observacoes")
    private String observacoes;

    public Atividade() {
    }

    public Atividade(
            Integer idAtividade,
            String nome,
            Integer idDisciplina,
            String tipo,
            String descricao,
            LocalDate dataDeEntrega,
            String status,
            BigDecimal notaMaxima,
            BigDecimal notaObtida,
            BigDecimal peso,
            String prioridade,
            String observacoes) {

        this.idAtividade = idAtividade;
        this.nome = nome;
        this.idDisciplina = idDisciplina;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataDeEntrega = dataDeEntrega;
        this.status = status;
        this.notaMaxima = notaMaxima;
        this.notaObtida = notaObtida;
        this.peso = peso;
        this.prioridade = prioridade;
        this.observacoes = observacoes;
    }

    public Integer getIdAtividade() {
        return idAtividade;
    }

    public void setIdAtividade(Integer idAtividade) {
        this.idAtividade = idAtividade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(Integer idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataDeEntrega() {
        return dataDeEntrega;
    }

    public void setDataDeEntrega(LocalDate dataDeEntrega) {
        this.dataDeEntrega = dataDeEntrega;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(BigDecimal notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public BigDecimal getNotaObtida() {
        return notaObtida;
    }

    public void setNotaObtida(BigDecimal notaObtida) {
        this.notaObtida = notaObtida;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}