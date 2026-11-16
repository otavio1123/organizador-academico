package com.organizador.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.organizador.api.model.Curso;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.CursoRepository;
import com.organizador.api.repository.LogAuditoriaRepository;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;

    public CursoService(
            CursoRepository cursoRepository,
            LogAuditoriaRepository logAuditoriaRepository) {

        this.cursoRepository = cursoRepository;
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

        Curso cursoSalvo =
                cursoRepository.save(curso);

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
                novosDados.getStatus()
        );

        Curso cursoAtualizado =
                cursoRepository.save(curso);

        registrarAuditoria(
                usuario,
                "Editou o curso "
                        + cursoAtualizado.getNome()
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

        cursoRepository.delete(curso);

        registrarAuditoria(
                usuario,
                "Excluiu o curso "
                        + nomeCurso
        );

        return true;
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