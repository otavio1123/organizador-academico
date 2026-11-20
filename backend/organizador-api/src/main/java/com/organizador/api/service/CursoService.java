package com.organizador.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.organizador.api.model.Curso;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.CursoRepository;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.SemestreRepository;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final SemestreRepository semestreRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;

    public CursoService(
            CursoRepository cursoRepository,
            SemestreRepository semestreRepository,
            LogAuditoriaRepository logAuditoriaRepository) {

        this.cursoRepository = cursoRepository;
        this.semestreRepository = semestreRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    public List<Curso> listar(
            Usuario usuario) {

        return cursoRepository
                .findByIdUsuario(
                        usuario.getId()
                );
    }

    public Curso cadastrar(
            Curso curso,
            Usuario usuario) {

        curso.setIdCurso(null);

        curso.setIdUsuario(
                usuario.getId()
        );

        if (curso.getStatus() == null
                || curso.getStatus().isBlank()) {

            curso.setStatus("ATIVO");
        }

        validarStatus(
                curso.getStatus()
        );

        Curso cursoSalvo =
                cursoRepository.save(
                        curso
                );

        registrarAuditoria(
                usuario,
                "Cadastrou o curso "
                        + cursoSalvo.getNome()
        );

        return cursoSalvo;
    }

    public Optional<Curso> buscarDoUsuario(
            Integer idCurso,
            Usuario usuario) {

        return cursoRepository
                .findByIdCursoAndIdUsuario(
                        idCurso,
                        usuario.getId()
                );
    }

    @Transactional
    public Curso atualizar(
            Integer idCurso,
            Curso novosDados,
            Usuario usuario) {

        Optional<Curso> cursoEncontrado =
                buscarDoUsuario(
                        idCurso,
                        usuario
                );

        if (cursoEncontrado.isEmpty()) {
            return null;
        }

        Curso curso =
                cursoEncontrado.get();

        String statusAnterior =
                curso.getStatus();

        String novoStatus =
                novosDados.getStatus();

        if (novoStatus == null
                || novoStatus.isBlank()) {

            novoStatus =
                    statusAnterior;
        }

        validarStatus(
                novoStatus
        );

        validarTransicaoStatus(
                statusAnterior,
                novoStatus
        );

        curso.setNome(
                novosDados.getNome()
        );

        curso.setInstituicao(
                novosDados.getInstituicao()
        );

        curso.setTipo(
                novosDados.getTipo()
        );

        curso.setStatus(
                novoStatus
        );

        if ("TRANCADO".equals(novoStatus)
                || "CONCLUIDO".equals(novoStatus)) {

            desativarSemestreAtivo(
                    curso.getIdCurso()
            );
        }

        Curso cursoAtualizado =
                cursoRepository.save(
                        curso
                );

        registrarAlteracaoStatus(
                usuario,
                cursoAtualizado,
                statusAnterior,
                novoStatus
        );

        return cursoAtualizado;
    }

    public boolean excluir(
            Integer idCurso,
            Usuario usuario) {

        Optional<Curso> cursoEncontrado =
                buscarDoUsuario(
                        idCurso,
                        usuario
                );

        if (cursoEncontrado.isEmpty()) {
            return false;
        }

        Curso curso =
                cursoEncontrado.get();

        String nomeCurso =
                curso.getNome();

        cursoRepository.delete(
                curso
        );

        registrarAuditoria(
                usuario,
                "Excluiu o curso "
                        + nomeCurso
        );

        return true;
    }

    private void desativarSemestreAtivo(
            Integer idCurso) {

        Optional<Semestre> semestreAtivo =
                semestreRepository
                        .findFirstByIdCursoAndAtivo(
                                idCurso,
                                "S"
                        );

        if (semestreAtivo.isEmpty()) {
            return;
        }

        Semestre semestre =
                semestreAtivo.get();

        semestre.setAtivo(
                "N"
        );

        semestreRepository.save(
                semestre
        );
    }

    private void registrarAlteracaoStatus(
            Usuario usuario,
            Curso curso,
            String statusAnterior,
            String novoStatus) {

        if (statusAnterior != null
                && statusAnterior.equals(
                        novoStatus
                )) {

            registrarAuditoria(
                    usuario,
                    "Editou o curso "
                            + curso.getNome()
            );

            return;
        }

        String acao;

        if ("TRANCADO".equals(
                novoStatus
        )) {

            acao =
                    "Trancou o curso "
                            + curso.getNome();

        } else if ("CONCLUIDO".equals(
                novoStatus
        )) {

            acao =
                    "Concluiu o curso "
                            + curso.getNome();

        } else if ("ATIVO".equals(
                novoStatus
        )
                && "TRANCADO".equals(
                        statusAnterior
                )) {

            acao =
                    "Reativou o curso "
                            + curso.getNome();

        } else if ("ATIVO".equals(
                novoStatus
        )
                && "CONCLUIDO".equals(
                        statusAnterior
                )) {

            acao =
                    "Reabriu o curso "
                            + curso.getNome();

        } else {

            acao =
                    "Editou o curso "
                            + curso.getNome();
        }

        registrarAuditoria(
                usuario,
                acao
        );
    }

    private void validarTransicaoStatus(
            String statusAnterior,
            String novoStatus) {

        if (statusAnterior == null
                || statusAnterior.equals(
                        novoStatus
                )) {

            return;
        }

        if ("TRANCADO".equals(statusAnterior)
                && "CONCLUIDO".equals(novoStatus)) {

            throw new IllegalStateException(
                    "Reative o curso antes de marcá-lo como concluído."
            );
        }

        if ("CONCLUIDO".equals(statusAnterior)
                && "TRANCADO".equals(novoStatus)) {

            throw new IllegalStateException(
                    "Reabra o curso antes de marcá-lo como trancado."
            );
        }
    }

    private void validarStatus(
            String status) {

        if (!"ATIVO".equals(status)
                && !"TRANCADO".equals(status)
                && !"CONCLUIDO".equals(status)) {

            throw new IllegalArgumentException(
                    "Status do curso inválido."
            );
        }
    }

    private void registrarAuditoria(
            Usuario usuario,
            String acao) {

        LogAuditoria log =
                new LogAuditoria();

        log.setIdUsuario(
                Long.valueOf(
                        usuario.getId()
                )
        );

        log.setNomeUsuario(
                usuario.getNome()
        );

        log.setAcao(
                acao
        );

        logAuditoriaRepository.save(
                log
        );
    }
}