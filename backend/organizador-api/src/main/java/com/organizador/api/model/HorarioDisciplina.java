package com.organizador.api.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"HorarioDisciplina\"")
public class HorarioDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"IDHorario\"")
    private Integer idHorario;

    @Column(name = "\"IDDisciplina\"")
    private Integer idDisciplina;

    @Column(name = "\"DiaSemana\"")
    private String diaSemana;

    @Column(name = "\"HoraInicio\"")
    private LocalTime horaInicio;

    @Column(name = "\"HoraFim\"")
    private LocalTime horaFim;

    public HorarioDisciplina() {
    }

    public HorarioDisciplina(
            Integer idHorario,
            Integer idDisciplina,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFim
    ) {
        this.idHorario = idHorario;
        this.idDisciplina = idDisciplina;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public Integer getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(Integer idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
}