package com.organizador.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.organizador.api.model.Atividade;
import com.organizador.api.model.Disciplina;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.AtividadeRepository;
import com.organizador.api.repository.DisciplinaRepository;
import com.organizador.api.repository.SemestreRepository;

@Service
public class DashboardService {

    private final SemestreRepository semestreRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final AtividadeRepository atividadeRepository;

    public DashboardService(
            SemestreRepository semestreRepository,
            DisciplinaRepository disciplinaRepository,
            AtividadeRepository atividadeRepository) {

        this.semestreRepository = semestreRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.atividadeRepository = atividadeRepository;
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
         * Busca somente os semestres do usuário autenticado
         * e depois filtra pelo curso selecionado.
         *
         * Dessa forma, o Dashboard nunca mistura dados
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

        /*
         * As disciplinas carregadas pertencem somente
         * ao semestre ativo do curso selecionado.
         */
        List<Disciplina> disciplinas =
                disciplinaRepository
                        .findByIdSemestre(
                                semestre.getIdSemestre()
                        );

        /*
         * ============================
         * DADOS DAS DISCIPLINAS
         * ============================
         */

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

        Map<Integer, String> nomesDisciplinas =
                new LinkedHashMap<>();

        for (Disciplina disciplina : disciplinas) {

            nomesDisciplinas.put(
                    disciplina.getIdDisciplina(),
                    disciplina.getNome()
            );

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

        /*
         * ============================
         * ATIVIDADES DO CURSO
         * ============================
         *
         * Primeiro pegamos somente os IDs das disciplinas
         * do semestre ativo.
         *
         * Depois buscamos atividades somente dessas
         * disciplinas.
         *
         * Portanto:
         *
         * Curso selecionado
         *      ↓
         * Semestre ativo
         *      ↓
         * Disciplinas
         *      ↓
         * Atividades
         */

        List<Integer> idsDisciplinas =
                disciplinas
                        .stream()
                        .map(
                                Disciplina::getIdDisciplina
                        )
                        .toList();

        List<Atividade> atividades;

        if (idsDisciplinas.isEmpty()) {

            atividades =
                    List.of();

        } else {

            atividades =
                    atividadeRepository
                            .findByIdDisciplinaIn(
                                    idsDisciplinas
                            );
        }

        /*
         * Ordena as atividades pela data de entrega.
         *
         * Atividades sem data ficam por último.
         */
        List<Atividade> atividadesOrdenadas =
                new ArrayList<>(
                        atividades
                );

        atividadesOrdenadas.sort(
                Comparator.comparing(
                        Atividade::getDataDeEntrega,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                )
        );

        LocalDate hoje =
                LocalDate.now();

        int atividadesPendentes = 0;
        int atividadesEmAndamento = 0;
        int atividadesAtrasadas = 0;
        int atividadesConcluidas = 0;

        List<Map<String, Object>> dadosAtividades =
                new ArrayList<>();

        for (Atividade atividade :
                atividadesOrdenadas) {

            String statusVisual =
                    obterStatusVisual(
                            atividade,
                            hoje
                    );

            switch (statusVisual) {

                case "ATRASADA" ->
                        atividadesAtrasadas++;

                case "CONCLUIDA" ->
                        atividadesConcluidas++;

                case "EM_ANDAMENTO" ->
                        atividadesEmAndamento++;

                case "PENDENTE" ->
                        atividadesPendentes++;

                default ->
                        atividadesPendentes++;
            }

            dadosAtividades.add(
                    criarDadosAtividade(
                            atividade,
                            nomesDisciplinas,
                            hoje
                    )
            );
        }

        /*
         * ============================
         * PRÓXIMAS ENTREGAS
         * ============================
         *
         * Apenas atividades:
         *
         * - não concluídas
         * - com data de entrega
         * - com entrega hoje ou no futuro
         *
         * Retorna no máximo 5.
         */

        List<Map<String, Object>> proximasEntregas =
                new ArrayList<>();

        for (Atividade atividade :
                atividadesOrdenadas) {

            if (proximasEntregas.size() >= 5) {
                break;
            }

            String statusOriginal =
                    obterStatusOriginal(
                            atividade
                    );

            if ("CONCLUIDA".equals(
                    statusOriginal
            )) {

                continue;
            }

            LocalDate dataEntrega =
                    atividade.getDataDeEntrega();

            if (dataEntrega == null) {
                continue;
            }

            if (dataEntrega.isBefore(
                    hoje
            )) {

                continue;
            }

            proximasEntregas.add(
                    criarDadosAtividade(
                            atividade,
                            nomesDisciplinas,
                            hoje
                    )
            );
        }

        /*
         * ============================
         * RESPOSTA DO DASHBOARD
         * ============================
         */

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

        /*
         * Dados das atividades.
         */
        resposta.put(
                "totalAtividades",
                atividades.size()
        );

        resposta.put(
                "atividadesPendentes",
                atividadesPendentes
        );

        resposta.put(
                "atividadesEmAndamento",
                atividadesEmAndamento
        );

        resposta.put(
                "atividadesAtrasadas",
                atividadesAtrasadas
        );

        resposta.put(
                "atividadesConcluidas",
                atividadesConcluidas
        );

        resposta.put(
                "atividades",
                dadosAtividades
        );

        resposta.put(
                "proximasEntregas",
                proximasEntregas
        );

        return resposta;
    }

    /*
     * Retorna o status efetivamente salvo no banco.
     */
    private String obterStatusOriginal(
            Atividade atividade) {

        if (
                atividade.getStatus() == null
                        || atividade
                                .getStatus()
                                .isBlank()
        ) {

            return "PENDENTE";
        }

        return atividade
                .getStatus()
                .trim()
                .toUpperCase();
    }

    /*
     * ATRASADA não é gravada no banco.
     *
     * É calculada dinamicamente:
     *
     * data de entrega < hoje
     * e atividade não concluída.
     */
    private String obterStatusVisual(
            Atividade atividade,
            LocalDate hoje) {

        String statusOriginal =
                obterStatusOriginal(
                        atividade
                );

        if (
                atividade.getDataDeEntrega() != null
                        && atividade
                                .getDataDeEntrega()
                                .isBefore(hoje)
                        && !"CONCLUIDA".equals(
                                statusOriginal
                        )
        ) {

            return "ATRASADA";
        }

        return statusOriginal;
    }

    /*
     * Converte a atividade para um objeto simples
     * que será enviado para o frontend.
     */
    private Map<String, Object> criarDadosAtividade(
            Atividade atividade,
            Map<Integer, String> nomesDisciplinas,
            LocalDate hoje) {

        Map<String, Object> dados =
                new LinkedHashMap<>();

        dados.put(
                "idAtividade",
                atividade.getIdAtividade()
        );

        dados.put(
                "idDisciplina",
                atividade.getIdDisciplina()
        );

        dados.put(
                "disciplina",
                nomesDisciplinas.getOrDefault(
                        atividade.getIdDisciplina(),
                        "Disciplina"
                )
        );

        dados.put(
                "nome",
                atividade.getNome()
        );

        dados.put(
                "tipo",
                atividade.getTipo()
        );

        dados.put(
                "descricao",
                atividade.getDescricao()
        );

        dados.put(
                "dataDeEntrega",
                atividade.getDataDeEntrega()
        );

        dados.put(
                "status",
                obterStatusOriginal(
                        atividade
                )
        );

        dados.put(
                "statusVisual",
                obterStatusVisual(
                        atividade,
                        hoje
                )
        );

        dados.put(
                "prioridade",
                atividade.getPrioridade()
        );

        return dados;
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

        /*
         * Valores vazios das atividades.
         */
        resposta.put(
                "totalAtividades",
                0
        );

        resposta.put(
                "atividadesPendentes",
                0
        );

        resposta.put(
                "atividadesEmAndamento",
                0
        );

        resposta.put(
                "atividadesAtrasadas",
                0
        );

        resposta.put(
                "atividadesConcluidas",
                0
        );

        resposta.put(
                "atividades",
                List.of()
        );

        resposta.put(
                "proximasEntregas",
                List.of()
        );

        return resposta;
    }
}