package com.organizador.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.organizador.api.model.Disciplina;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.DisciplinaRepository;
import com.organizador.api.repository.SemestreRepository;

@Service
public class DashboardService {

    private final SemestreRepository semestreRepository;
    private final DisciplinaRepository disciplinaRepository;

    public DashboardService(
            SemestreRepository semestreRepository,
            DisciplinaRepository disciplinaRepository) {

        this.semestreRepository = semestreRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public Map<String, Object> obterDashboard(
            Usuario usuario,
            Integer idCurso) {

        Map<String, Object> resposta =
                new LinkedHashMap<>();

        if (idCurso == null) {

            return criarDashboardVazio(
                    resposta,
                    null
            );
        }

        /*
         * Primeiro busca somente os semestres
         * pertencentes ao usuário autenticado.
         *
         * Depois filtra pelo curso selecionado.
         *
         * Isso evita misturar semestres e disciplinas
         * de cursos diferentes.
         */
        Optional<Semestre> semestreAtivo =
                semestreRepository
                        .findByIdusuario(
                                usuario.getId()
                        )
                        .stream()
                        .filter(semestre ->
                                semestre.getIdCurso() != null
                                        && semestre
                                                .getIdCurso()
                                                .equals(idCurso)
                        )
                        .filter(semestre ->
                                "S".equalsIgnoreCase(
                                        semestre.getAtivo()
                                )
                        )
                        .findFirst();

        if (semestreAtivo.isEmpty()) {

            return criarDashboardVazio(
                    resposta,
                    idCurso
            );
        }

        Semestre semestre =
                semestreAtivo.get();

        List<Disciplina> disciplinas =
                disciplinaRepository
                        .findByIdSemestre(
                                semestre.getIdSemestre()
                        );

        int totalFaltas =
                disciplinas
                        .stream()
                        .mapToInt(disciplina ->
                                disciplina.getFaltas() == null
                                        ? 0
                                        : disciplina.getFaltas()
                        )
                        .sum();

        List<BigDecimal> medias =
                disciplinas
                        .stream()
                        .map(
                                Disciplina::getMediaFinal
                        )
                        .filter(
                                media -> media != null
                        )
                        .toList();

        BigDecimal mediaGeral = null;

        if (!medias.isEmpty()) {

            BigDecimal soma =
                    medias
                            .stream()
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            mediaGeral =
                    soma.divide(
                            BigDecimal.valueOf(
                                    medias.size()
                            ),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        long disciplinasAbaixoMedia =
                disciplinas
                        .stream()
                        .filter(disciplina ->
                                disciplina.getMediaMinima() != null
                                        && disciplina.getMediaFinal() != null
                        )
                        .filter(disciplina ->
                                disciplina
                                        .getMediaFinal()
                                        .compareTo(
                                                disciplina.getMediaMinima()
                                        ) < 0
                        )
                        .count();

        List<Map<String, Object>> dadosDisciplinas =
                new ArrayList<>();

        for (Disciplina disciplina : disciplinas) {

            Map<String, Object> dados =
                    new LinkedHashMap<>();

            dados.put(
                    "idDisciplina",
                    disciplina.getIdDisciplina()
            );

            dados.put(
                    "nome",
                    disciplina.getNome()
            );

            dados.put(
                    "professor",
                    disciplina.getNomeProfessor()
            );

            dados.put(
                    "faltas",
                    disciplina.getFaltas() == null
                            ? 0
                            : disciplina.getFaltas()
            );

            dados.put(
                    "mediaMinima",
                    disciplina.getMediaMinima()
            );

            dados.put(
                    "mediaFinal",
                    disciplina.getMediaFinal()
            );

            dadosDisciplinas.add(
                    dados
            );
        }

        resposta.put(
                "idCurso",
                idCurso
        );

        resposta.put(
                "possuiSemestreAtivo",
                true
        );

        resposta.put(
                "idSemestre",
                semestre.getIdSemestre()
        );

        resposta.put(
                "semestre",
                semestre.getNome()
        );

        resposta.put(
                "totalDisciplinas",
                disciplinas.size()
        );

        resposta.put(
                "totalFaltas",
                totalFaltas
        );

        resposta.put(
                "mediaGeral",
                mediaGeral
        );

        resposta.put(
                "disciplinasAbaixoMedia",
                disciplinasAbaixoMedia
        );

        resposta.put(
                "disciplinas",
                dadosDisciplinas
        );

        return resposta;
    }

    private Map<String, Object> criarDashboardVazio(
            Map<String, Object> resposta,
            Integer idCurso) {

        resposta.put(
                "idCurso",
                idCurso
        );

        resposta.put(
                "possuiSemestreAtivo",
                false
        );

        resposta.put(
                "idSemestre",
                null
        );

        resposta.put(
                "semestre",
                null
        );

        resposta.put(
                "totalDisciplinas",
                0
        );

        resposta.put(
                "totalFaltas",
                0
        );

        resposta.put(
                "mediaGeral",
                null
        );

        resposta.put(
                "disciplinasAbaixoMedia",
                0
        );

        resposta.put(
                "disciplinas",
                List.of()
        );

        return resposta;
    }
}