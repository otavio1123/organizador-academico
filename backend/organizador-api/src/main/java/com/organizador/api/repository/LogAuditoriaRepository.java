package com.organizador.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.organizador.api.model.LogAuditoria;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

}