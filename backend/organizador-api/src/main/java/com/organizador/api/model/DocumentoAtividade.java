package com.organizador.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "documentosatividade")
public class DocumentoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddocumento")
    private Integer idDocumento;

    @Column(name = "idatividade")
    private Integer idAtividade;

    @Column(name = "nomeoriginal")
    private String nomeOriginal;

    @Column(name = "caminhostorage")
    private String caminhoStorage;

    @Column(name = "tipomime")
    private String tipoMime;

    @Column(name = "tamanhobytes")
    private Long tamanhoBytes;

    @Column(name = "dataupload")
    private LocalDateTime dataUpload;

    public DocumentoAtividade() {
    }

    public DocumentoAtividade(
            Integer idDocumento,
            Integer idAtividade,
            String nomeOriginal,
            String caminhoStorage,
            String tipoMime,
            Long tamanhoBytes,
            LocalDateTime dataUpload) {

        this.idDocumento = idDocumento;
        this.idAtividade = idAtividade;
        this.nomeOriginal = nomeOriginal;
        this.caminhoStorage = caminhoStorage;
        this.tipoMime = tipoMime;
        this.tamanhoBytes = tamanhoBytes;
        this.dataUpload = dataUpload;
    }

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Integer getIdAtividade() {
        return idAtividade;
    }

    public void setIdAtividade(Integer idAtividade) {
        this.idAtividade = idAtividade;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getCaminhoStorage() {
        return caminhoStorage;
    }

    public void setCaminhoStorage(String caminhoStorage) {
        this.caminhoStorage = caminhoStorage;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }
}