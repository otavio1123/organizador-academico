package com.organizador.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.organizador.api.model.Disciplina;
import com.organizador.api.model.HorarioDisciplina;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.DisciplinaRepository;
import com.organizador.api.repository.HorarioDisciplinaRepository;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.SemestreRepository;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final SemestreRepository semestreRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final HorarioDisciplinaRepository horarioDisciplinaRepository;

    public DisciplinaService(
            DisciplinaRepository disciplinaRepository,
            SemestreRepository semestreRepository,
            LogAuditoriaRepository logAuditoriaRepository,
            HorarioDisciplinaRepository horarioDisciplinaRepository) {

        this.disciplinaRepository = disciplinaRepository;
        this.semestreRepository = semestreRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
        this.horarioDisciplinaRepository = horarioDisciplinaRepository;
    }

    public boolean semestrePertenceAoUsuario(
            Integer idSemestre,
            Integer idUsuario) {

        return semestreRepository
                .findByIdSemestreAndIdusuario(
                        idSemestre,
                        idUsuario
                )
                .isPresent();
    }

    public List<Disciplina> listarPorSemestre(
            Integer idSemestre,
            Usuario usuario) {

        if (!semestrePertenceAoUsuario(
                idSemestre,
                usuario.getId())) {

            return List.of();
        }

        return disciplinaRepository
                .findByIdSemestre(idSemestre);
    }

    public Disciplina cadastrar(
            Integer idSemestre,
            Disciplina disciplina,
            Usuario usuario) {

        if (!semestrePertenceAoUsuario(
                idSemestre,
                usuario.getId())) {

            return null;
        }

        if (disciplina.getFaltas() != null
                && disciplina.getFaltas() < 0) {

            return null;
        }

        if (disciplina.getLimiteFaltas() != null
                && disciplina.getLimiteFaltas() <= 0) {

            return null;
        }

        disciplina.setIdDisciplina(null);
        disciplina.setIdSemestre(idSemestre);

        if (disciplina.getFaltas() == null) {
            disciplina.setFaltas(0);
        }

        Disciplina disciplinaSalva =
                disciplinaRepository.save(disciplina);

        registrarAuditoria(
                usuario,
                "Cadastrou a disciplina "
                        + disciplinaSalva.getNome()
        );

        return disciplinaSalva;
    }

    public Optional<Disciplina> buscarDoUsuario(
            Integer idDisciplina,
            Usuario usuario) {

        Optional<Disciplina> disciplinaEncontrada =
                disciplinaRepository.findById(idDisciplina);

        if (disciplinaEncontrada.isEmpty()) {
            return Optional.empty();
        }

        Disciplina disciplina =
                disciplinaEncontrada.get();

        if (!semestrePertenceAoUsuario(
                disciplina.getIdSemestre(),
                usuario.getId())) {

            return Optional.empty();
        }

        return Optional.of(disciplina);
    }

    public Disciplina atualizar(
            Integer idDisciplina,
            Disciplina novosDados,
            Usuario usuario) {

        Optional<Disciplina> disciplinaEncontrada =
                buscarDoUsuario(
                        idDisciplina,
                        usuario
                );

        if (disciplinaEncontrada.isEmpty()) {
            return null;
        }

        if (novosDados.getFaltas() != null
                && novosDados.getFaltas() < 0) {

            return null;
        }

        if (novosDados.getLimiteFaltas() != null
                && novosDados.getLimiteFaltas() <= 0) {

            return null;
        }

        Disciplina disciplina =
                disciplinaEncontrada.get();

        disciplina.setNome(
                novosDados.getNome()
        );

        disciplina.setNomeProfessor(
                novosDados.getNomeProfessor()
        );

        disciplina.setFaltas(
                novosDados.getFaltas() == null
                        ? 0
                        : novosDados.getFaltas()
        );

        disciplina.setLimiteFaltas(
                novosDados.getLimiteFaltas()
        );

        disciplina.setMediaMinima(
                novosDados.getMediaMinima()
        );

        disciplina.setMediaFinal(
                novosDados.getMediaFinal()
        );

        Disciplina disciplinaAtualizada =
                disciplinaRepository.save(disciplina);

        registrarAuditoria(
                usuario,
                "Editou a disciplina "
                        + disciplinaAtualizada.getNome()
        );

        return disciplinaAtualizada;
    }

    public Disciplina adicionarFalta(
        Integer idDisciplina,
        Usuario usuario) {

    Optional<Disciplina> disciplinaEncontrada =
            buscarDoUsuario(
                    idDisciplina,
                    usuario
            );

    if (disciplinaEncontrada.isEmpty()) {
        return null;
    }

    Disciplina disciplina =
            disciplinaEncontrada.get();

    int faltasAtuais =
            disciplina.getFaltas() == null
                    ? 0
                    : disciplina.getFaltas();

    disciplina.setFaltas(
            faltasAtuais + 1
    );

    Disciplina disciplinaAtualizada =
            disciplinaRepository.save(disciplina);

    registrarAuditoria(
            usuario,
            "Adicionou falta na disciplina "
                    + disciplinaAtualizada.getNome()
    );

    return disciplinaAtualizada;
}

public Disciplina removerFalta(
        Integer idDisciplina,
        Usuario usuario) {

    Optional<Disciplina> disciplinaEncontrada =
            buscarDoUsuario(
                    idDisciplina,
                    usuario
            );

    if (disciplinaEncontrada.isEmpty()) {
        return null;
    }

    Disciplina disciplina =
            disciplinaEncontrada.get();

    int faltasAtuais =
            disciplina.getFaltas() == null
                    ? 0
                    : disciplina.getFaltas();

    if (faltasAtuais <= 0) {
        return null;
    }

    disciplina.setFaltas(
            faltasAtuais - 1
    );

    Disciplina disciplinaAtualizada =
            disciplinaRepository.save(disciplina);

    registrarAuditoria(
            usuario,
            "Removeu falta da disciplina "
                    + disciplinaAtualizada.getNome()
    );

    return disciplinaAtualizada;
}

    public List<HorarioDisciplina> listarHorarios(
            Integer idDisciplina,
            Usuario usuario) {

        Optional<Disciplina> disciplinaEncontrada =
                buscarDoUsuario(
                        idDisciplina,
                        usuario
                );

        if (disciplinaEncontrada.isEmpty()) {
            return List.of();
        }

        return horarioDisciplinaRepository
                .findByIdDisciplina(
                        idDisciplina
                );
    }

    public HorarioDisciplina cadastrarHorario(
            Integer idDisciplina,
            HorarioDisciplina horario,
            Usuario usuario) {

        Optional<Disciplina> disciplinaEncontrada =
                buscarDoUsuario(
                        idDisciplina,
                        usuario
                );

        if (disciplinaEncontrada.isEmpty()) {
            return null;
        }

        if (horario.getDiaSemana() == null
                || horario.getDiaSemana()
                        .trim()
                        .isEmpty()) {

            return null;
        }

        if (horario.getHoraInicio() != null
                && horario.getHoraFim() != null
                && !horario.getHoraFim()
                        .isAfter(
                                horario.getHoraInicio()
                        )) {

            return null;
        }

        horario.setIdHorario(null);
        horario.setIdDisciplina(idDisciplina);

        HorarioDisciplina horarioSalvo =
                horarioDisciplinaRepository.save(
                        horario
                );

        registrarAuditoria(
                usuario,
                "Adicionou horário à disciplina "
                        + disciplinaEncontrada
                                .get()
                                .getNome()
        );

        return horarioSalvo;
    }

    @Transactional
    public boolean excluirHorario(
            Integer idHorario,
            Usuario usuario) {

        Optional<HorarioDisciplina> horarioEncontrado =
                horarioDisciplinaRepository
                        .findById(idHorario);

        if (horarioEncontrado.isEmpty()) {
            return false;
        }

        HorarioDisciplina horario =
                horarioEncontrado.get();

        Optional<Disciplina> disciplinaEncontrada =
                buscarDoUsuario(
                        horario.getIdDisciplina(),
                        usuario
                );

        if (disciplinaEncontrada.isEmpty()) {
            return false;
        }

        horarioDisciplinaRepository.delete(
                horario
        );

        registrarAuditoria(
                usuario,
                "Excluiu horário da disciplina "
                        + disciplinaEncontrada
                                .get()
                                .getNome()
        );

        return true;
    }

    @Transactional
    public boolean excluir(
            Integer idDisciplina,
            Usuario usuario) {

        Optional<Disciplina> disciplinaEncontrada =
                buscarDoUsuario(
                        idDisciplina,
                        usuario
                );

        if (disciplinaEncontrada.isEmpty()) {
            return false;
        }

        Disciplina disciplina =
                disciplinaEncontrada.get();

        String nomeDisciplina =
                disciplina.getNome();

        horarioDisciplinaRepository
                .deleteByIdDisciplina(
                        idDisciplina
                );

        disciplinaRepository.delete(
                disciplina
        );

        registrarAuditoria(
                usuario,
                "Excluiu a disciplina "
                        + nomeDisciplina
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