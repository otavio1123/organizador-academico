package com.organizador.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.organizador.api.model.LogAuditoria;
import com.organizador.api.repository.LogAuditoriaRepository;

@RestController
@CrossOrigin(origins = "*")
public class LogAuditoriaController {

    private final LogAuditoriaRepository logAuditoriaRepository;

    public LogAuditoriaController(LogAuditoriaRepository logAuditoriaRepository) {
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    @PostMapping("/LogAuditoria")
    public ResponseEntity<?> criar(@RequestBody LogAuditoria logAuditoria) {

        LogAuditoria logSalvo = logAuditoriaRepository.save(logAuditoria);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(logSalvo);
    }
}