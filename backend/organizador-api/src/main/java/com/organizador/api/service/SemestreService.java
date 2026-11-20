package com.organizador.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.organizador.api.model.Curso;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.CursoRepository;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.SemestreRepository;

@Service
public class SemestreService {

    private final SemestreRepository semestreRepository;
    private final CursoRepository cursoRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;

    public SemestreService(
            SemestreRepository semestreRepository,
            CursoRepository cursoRepository,
            LogAuditoriaRepository logAuditoriaRepository) {

        this.semestreRepository = semestreRepository;
        this.cursoRepository = cursoRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    public List<Semestre> listar(
            Usuario usuario) {

        return semestreRepository
                .findByIdusuario(
                        usuario.getId()
                );
    }

    public List<Semestre> listarPorCurso(
            Integer idCurso,
            Usuario usuario) {

        if (!cursoPertenceAoUsuario(
                idCurso,
                usuario)) {

            return List.of();
        }

        return semestreRepository
                .findByIdusuarioAndIdCurso(
                        usuario.getId(),
                        idCurso
                );
    }

    public boolean cursoPertenceAoUsuario(
            Integer idCurso,
            Usuario usuario) {

        if (idCurso == null) {
            return false;
        }

        return cursoRepository
                .findByIdCursoAndIdUsuario(
                        idCurso,
                        usuario.getId()
                )
                .isPresent();
    }

    public Optional<Semestre> buscarDoUsuario(
            Integer idSemestre,
            Usuario usuario) {

        return semestreRepository
                .findByIdSemestreAndIdusuario(
                        idSemestre,
                        usuario.getId()
                );
    }

    public Semestre cadastrar(
            Semestre semestre,
            Usuario usuario) {

        if (semestre.getIdCurso() == null) {

            throw new IllegalArgumentException(
                    "Selecione um curso."
            );
        }

        validarCursoAtivo(
                semestre.getIdCurso(),
                usuario
        );

        if (semestre.getDataInicio() != null
                && semestre.getDataFim() != null
                && semestre.getDataFim()
                        .isBefore(
                                semestre.getDataInicio()
                        )) {

            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial."
            );
        }

        if ("S".equals(semestre.getAtivo())
                && semestreRepository
                        .existsByIdCursoAndAtivo(
                                semestre.getIdCurso(),
                                "S"
                        )) {

            throw new IllegalStateException(
                    "Já existe um semestre ativo para este curso."
            );
        }

        semestre.setIdSemestre(null);

        semestre.setIdusuario(
                usuario.getId()
        );

        if (semestre.getAtivo() == null
                || semestre.getAtivo().isBlank()) {

            semestre.setAtivo("N");
        }

        Semestre semestreSalvo =
                semestreRepository.save(
                        semestre
                );

        registrarAuditoria(
                usuario,
                "Cadastrou o semestre "
                        + semestreSalvo.getNome()
        );

        return semestreSalvo;
    }

    public Semestre atualizar(
            Integer idSemestre,
            Semestre novosDados,
            Usuario usuario) {

        Optional<Semestre> semestreEncontrado =
                buscarDoUsuario(
                        idSemestre,
                        usuario
                );

        if (semestreEncontrado.isEmpty()) {
            return null;
        }

        Semestre semestre =
                semestreEncontrado.get();

        validarCursoAtivo(
                semestre.getIdCurso(),
                usuario
        );

        Integer idCurso =
                novosDados.getIdCurso() != null
                        ? novosDados.getIdCurso()
                        : semestre.getIdCurso();

        if (!idCurso.equals(
                semestre.getIdCurso()
        )) {

            validarCursoAtivo(
                    idCurso,
                    usuario
            );
        }

        if (novosDados.getDataInicio() != null
                && novosDados.getDataFim() != null
                && novosDados.getDataFim()
                        .isBefore(
                                novosDados.getDataInicio()
                        )) {

            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial."
            );
        }

        if ("S".equals(
                novosDados.getAtivo()
        )) {

            Optional<Semestre> semestreAtivo =
                    semestreRepository
                            .findFirstByIdCursoAndAtivo(
                                    idCurso,
                                    "S"
                            );

            if (semestreAtivo.isPresent()
                    && !semestreAtivo
                            .get()
                            .getIdSemestre()
                            .equals(idSemestre)) {

                throw new IllegalStateException(
                        "Já existe um semestre ativo para este curso."
                );
            }
        }

        semestre.setIdCurso(
                idCurso
        );

        semestre.setIdusuario(
                usuario.getId()
        );

        semestre.setNome(
                novosDados.getNome()
        );

        semestre.setDataInicio(
                novosDados.getDataInicio()
        );

        semestre.setDataFim(
                novosDados.getDataFim()
        );

        semestre.setAno(
                novosDados.getAno()
        );

        semestre.setAtivo(
                novosDados.getAtivo()
        );

        Semestre semestreAtualizado =
                semestreRepository.save(
                        semestre
                );

        registrarAuditoria(
                usuario,
                "Editou o semestre "
                        + semestreAtualizado.getNome()
        );

        return semestreAtualizado;
    }

    public boolean atualizarAtivo(
            Integer idSemestre,
            String ativo,
            Usuario usuario) {

        Optional<Semestre> semestreEncontrado =
                buscarDoUsuario(
                        idSemestre,
                        usuario
                );

        if (semestreEncontrado.isEmpty()) {
            return false;
        }

        Semestre semestre =
                semestreEncontrado.get();

        validarCursoAtivo(
                semestre.getIdCurso(),
                usuario
        );

        if ("S".equals(ativo)) {

            Optional<Semestre> semestreAtivo =
                    semestreRepository
                            .findFirstByIdCursoAndAtivo(
                                    semestre.getIdCurso(),
                                    "S"
                            );

            if (semestreAtivo.isPresent()
                    && !semestreAtivo
                            .get()
                            .getIdSemestre()
                            .equals(idSemestre)) {

                throw new IllegalStateException(
                        "Já existe um semestre ativo para este curso."
                );
            }
        }

        semestre.setAtivo(
                ativo
        );

        semestreRepository.save(
                semestre
        );

        registrarAuditoria(
                usuario,
                ("S".equals(ativo)
                        ? "Ativou o semestre "
                        : "Desativou o semestre ")
                        + semestre.getNome()
        );

        return true;
    }

    public boolean excluir(
            Integer idSemestre,
            Usuario usuario) {

        Optional<Semestre> semestreEncontrado =
                buscarDoUsuario(
                        idSemestre,
                        usuario
                );

        if (semestreEncontrado.isEmpty()) {
            return false;
        }

        Semestre semestre =
                semestreEncontrado.get();

        validarCursoAtivo(
                semestre.getIdCurso(),
                usuario
        );

        String nomeSemestre =
                semestre.getNome();

        semestreRepository.delete(
                semestre
        );

        registrarAuditoria(
                usuario,
                "Excluiu o semestre "
                        + nomeSemestre
        );

        return true;
    }

    private void validarCursoAtivo(
            Integer idCurso,
            Usuario usuario) {

        Curso curso =
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                idCurso,
                                usuario.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Curso não encontrado."
                                        )
                        );

        if ("TRANCADO".equals(
                curso.getStatus()
        )) {

            throw new IllegalStateException(
                    "Não é possível alterar semestres de um curso trancado."
            );
        }

        if ("CONCLUIDO".equals(
                curso.getStatus()
        )) {

            throw new IllegalStateException(
                    "Não é possível alterar semestres de um curso concluído."
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