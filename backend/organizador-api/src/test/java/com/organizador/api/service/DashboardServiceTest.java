package com.organizador.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.organizador.api.model.Atividade;
import com.organizador.api.model.Disciplina;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.AtividadeRepository;
import com.organizador.api.repository.DisciplinaRepository;
import com.organizador.api.repository.SemestreRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private SemestreRepository semestreRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private AtividadeRepository atividadeRepository;

    @Mock
    private Usuario usuario;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void configurarUsuario() {

        lenient()
                .when(usuario.getId())
                .thenReturn(10);
    }

    @Test
    void deveRetornarDashboardVazioQuandoIdCursoForNull() {

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        null
                );

        assertNull(
                resultado.get("idCurso")
        );

        assertFalse(
                (Boolean) resultado.get(
                        "possuiSemestreAtivo"
                )
        );

        assertEquals(
                0,
                resultado.get("totalDisciplinas")
        );

        assertEquals(
                0,
                resultado.get("totalFaltas")
        );

        assertNull(
                resultado.get("mediaGeral")
        );

        assertEquals(
                0,
                resultado.get("totalAtividades")
        );

        assertTrue(
                ((List<?>) resultado.get(
                        "atividades"
                )).isEmpty()
        );

        assertTrue(
                ((List<?>) resultado.get(
                        "proximasEntregas"
                )).isEmpty()
        );

        verify(
                semestreRepository,
                never()
        ).findByIdusuario(
                10
        );
    }

    @Test
    void deveRetornarDashboardVazioQuandoNaoExisteSemestreAtivo() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
                );

        when(
                semestreRepository.findByIdusuario(
                        10
                )
        ).thenReturn(
                List.of(
                        semestre
                )
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        assertEquals(
                1,
                resultado.get("idCurso")
        );

        assertFalse(
                (Boolean) resultado.get(
                        "possuiSemestreAtivo"
                )
        );

        assertEquals(
                0,
                resultado.get("totalDisciplinas")
        );

        verify(
                disciplinaRepository,
                never()
        ).findByIdSemestre(
                1
        );
    }

    @Test
    void deveConsiderarSomenteSemestreAtivoDoCursoSelecionado() {

        Semestre semestreOutroCurso =
                criarSemestre(
                        1,
                        2,
                        "Outro semestre",
                        "S"
                );

        Semestre semestreCursoSelecionado =
                criarSemestre(
                        2,
                        1,
                        "Semestre correto",
                        "S"
                );

        when(
                semestreRepository.findByIdusuario(
                        10
                )
        ).thenReturn(
                List.of(
                        semestreOutroCurso,
                        semestreCursoSelecionado
                )
        );

        when(
                disciplinaRepository.findByIdSemestre(
                        2
                )
        ).thenReturn(
                List.of()
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        assertTrue(
                (Boolean) resultado.get(
                        "possuiSemestreAtivo"
                )
        );

        assertEquals(
                2,
                resultado.get("idSemestre")
        );

        assertEquals(
                "Semestre correto",
                resultado.get("semestre")
        );

        verify(
                disciplinaRepository
        ).findByIdSemestre(
                2
        );

        verify(
                atividadeRepository,
                never()
        ).findByIdDisciplinaIn(
                anyList()
        );
    }

    @Test
    void deveCalcularDadosDasDisciplinas() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "2º Semestre",
                        "S"
                );

        Disciplina disciplina1 =
                criarDisciplina(
                        1,
                        "Java",
                        2,
                        "7.0",
                        "8.0"
                );

        Disciplina disciplina2 =
                criarDisciplina(
                        2,
                        "Banco de Dados",
                        3,
                        "7.0",
                        "6.0"
                );

        Disciplina disciplina3 =
                criarDisciplina(
                        3,
                        "Redes",
                        null,
                        "7.0",
                        null
                );

        when(
                semestreRepository.findByIdusuario(
                        10
                )
        ).thenReturn(
                List.of(
                        semestre
                )
        );

        when(
                disciplinaRepository.findByIdSemestre(
                        1
                )
        ).thenReturn(
                List.of(
                        disciplina1,
                        disciplina2,
                        disciplina3
                )
        );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(
                                1,
                                2,
                                3
                        )
                )
        ).thenReturn(
                List.of()
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        assertEquals(
                3,
                resultado.get("totalDisciplinas")
        );

        assertEquals(
                5,
                resultado.get("totalFaltas")
        );

        assertEquals(
                new BigDecimal("7.00"),
                resultado.get("mediaGeral")
        );

        assertEquals(
                1L,
                resultado.get("disciplinasAbaixoMedia")
        );

        List<?> disciplinas =
                (List<?>) resultado.get(
                        "disciplinas"
                );

        assertEquals(
                3,
                disciplinas.size()
        );
    }

    @Test
    void deveRetornarMediaGeralNullQuandoNaoExistemMedias() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "Semestre",
                        "S"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        "Java",
                        0,
                        "7.0",
                        null
                );

        when(
                semestreRepository.findByIdusuario(
                        10
                )
        ).thenReturn(
                List.of(
                        semestre
                )
        );

        when(
                disciplinaRepository.findByIdSemestre(
                        1
                )
        ).thenReturn(
                List.of(
                        disciplina
                )
        );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                List.of()
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        assertNull(
                resultado.get("mediaGeral")
        );
    }

    @Test
    void deveContarStatusDasAtividadesCorretamente() {

        configurarDashboardComDisciplina();

        Atividade pendente =
                criarAtividade(
                        1,
                        1,
                        "Atividade pendente",
                        "PENDENTE",
                        LocalDate.now().plusDays(5)
                );

        Atividade andamento =
                criarAtividade(
                        2,
                        1,
                        "Atividade em andamento",
                        "EM_ANDAMENTO",
                        LocalDate.now().plusDays(3)
                );

        Atividade atrasada =
                criarAtividade(
                        3,
                        1,
                        "Atividade atrasada",
                        "PENDENTE",
                        LocalDate.now().minusDays(1)
                );

        Atividade concluida =
                criarAtividade(
                        4,
                        1,
                        "Atividade concluída",
                        "CONCLUIDA",
                        LocalDate.now().minusDays(2)
                );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                List.of(
                        pendente,
                        andamento,
                        atrasada,
                        concluida
                )
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        assertEquals(
                4,
                resultado.get("totalAtividades")
        );

        assertEquals(
                1,
                resultado.get("atividadesPendentes")
        );

        assertEquals(
                1,
                resultado.get("atividadesEmAndamento")
        );

        assertEquals(
                1,
                resultado.get("atividadesAtrasadas")
        );

        assertEquals(
                1,
                resultado.get("atividadesConcluidas")
        );
    }

    @Test
    void deveConsiderarStatusVazioComoPendente() {

        configurarDashboardComDisciplina();

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Atividade",
                        " ",
                        LocalDate.now().plusDays(1)
                );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                List.of(
                        atividade
                )
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        assertEquals(
                1,
                resultado.get("atividadesPendentes")
        );

        List<?> atividades =
                (List<?>) resultado.get(
                        "atividades"
                );

        Map<?, ?> dados =
                (Map<?, ?>) atividades.get(0);

        assertEquals(
                "PENDENTE",
                dados.get("status")
        );

        assertEquals(
                "PENDENTE",
                dados.get("statusVisual")
        );
    }

    @Test
    void atividadeConcluidaAtrasadaNaoDeveSerMarcadaComoAtrasada() {

        configurarDashboardComDisciplina();

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Atividade concluída",
                        "CONCLUIDA",
                        LocalDate.now().minusDays(5)
                );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                List.of(
                        atividade
                )
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        assertEquals(
                0,
                resultado.get("atividadesAtrasadas")
        );

        assertEquals(
                1,
                resultado.get("atividadesConcluidas")
        );
    }

    @Test
    void deveRetornarSomenteProximasEntregasValidas() {

        configurarDashboardComDisciplina();

        Atividade atrasada =
                criarAtividade(
                        1,
                        1,
                        "Atrasada",
                        "PENDENTE",
                        LocalDate.now().minusDays(1)
                );

        Atividade semData =
                criarAtividade(
                        2,
                        1,
                        "Sem data",
                        "PENDENTE",
                        null
                );

        Atividade concluida =
                criarAtividade(
                        3,
                        1,
                        "Concluída",
                        "CONCLUIDA",
                        LocalDate.now().plusDays(1)
                );

        Atividade proxima1 =
                criarAtividade(
                        4,
                        1,
                        "Próxima 1",
                        "PENDENTE",
                        LocalDate.now()
                );

        Atividade proxima2 =
                criarAtividade(
                        5,
                        1,
                        "Próxima 2",
                        "EM_ANDAMENTO",
                        LocalDate.now().plusDays(2)
                );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                List.of(
                        proxima2,
                        concluida,
                        atrasada,
                        semData,
                        proxima1
                )
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        List<?> proximas =
                (List<?>) resultado.get(
                        "proximasEntregas"
                );

        assertEquals(
                2,
                proximas.size()
        );

        Map<?, ?> primeira =
                (Map<?, ?>) proximas.get(0);

        Map<?, ?> segunda =
                (Map<?, ?>) proximas.get(1);

        assertEquals(
                "Próxima 1",
                primeira.get("nome")
        );

        assertEquals(
                "Próxima 2",
                segunda.get("nome")
        );
    }

    @Test
    void deveLimitarProximasEntregasEmCinco() {

        configurarDashboardComDisciplina();

        List<Atividade> atividades =
                List.of(
                        criarAtividade(
                                1,
                                1,
                                "Atividade 1",
                                "PENDENTE",
                                LocalDate.now().plusDays(1)
                        ),
                        criarAtividade(
                                2,
                                1,
                                "Atividade 2",
                                "PENDENTE",
                                LocalDate.now().plusDays(2)
                        ),
                        criarAtividade(
                                3,
                                1,
                                "Atividade 3",
                                "PENDENTE",
                                LocalDate.now().plusDays(3)
                        ),
                        criarAtividade(
                                4,
                                1,
                                "Atividade 4",
                                "PENDENTE",
                                LocalDate.now().plusDays(4)
                        ),
                        criarAtividade(
                                5,
                                1,
                                "Atividade 5",
                                "PENDENTE",
                                LocalDate.now().plusDays(5)
                        ),
                        criarAtividade(
                                6,
                                1,
                                "Atividade 6",
                                "PENDENTE",
                                LocalDate.now().plusDays(6)
                        )
                );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                atividades
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        List<?> proximas =
                (List<?>) resultado.get(
                        "proximasEntregas"
                );

        assertEquals(
                5,
                proximas.size()
        );
    }

    @Test
    void deveOrdenarAtividadesPorDataDeEntrega() {

        configurarDashboardComDisciplina();

        Atividade depois =
                criarAtividade(
                        1,
                        1,
                        "Depois",
                        "PENDENTE",
                        LocalDate.now().plusDays(10)
                );

        Atividade antes =
                criarAtividade(
                        2,
                        1,
                        "Antes",
                        "PENDENTE",
                        LocalDate.now().plusDays(1)
                );

        Atividade semData =
                criarAtividade(
                        3,
                        1,
                        "Sem data",
                        "PENDENTE",
                        null
                );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                List.of(
                        depois,
                        semData,
                        antes
                )
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        List<?> atividades =
                (List<?>) resultado.get(
                        "atividades"
                );

        Map<?, ?> primeira =
                (Map<?, ?>) atividades.get(0);

        Map<?, ?> segunda =
                (Map<?, ?>) atividades.get(1);

        Map<?, ?> terceira =
                (Map<?, ?>) atividades.get(2);

        assertEquals(
                "Antes",
                primeira.get("nome")
        );

        assertEquals(
                "Depois",
                segunda.get("nome")
        );

        assertEquals(
                "Sem data",
                terceira.get("nome")
        );
    }

    @Test
    void deveUsarNomePadraoQuandoDisciplinaDaAtividadeNaoForEncontradaNoMapa() {

        configurarDashboardComDisciplina();

        Atividade atividade =
                criarAtividade(
                        1,
                        999,
                        "Atividade",
                        "PENDENTE",
                        LocalDate.now().plusDays(1)
                );

        when(
                atividadeRepository.findByIdDisciplinaIn(
                        List.of(1)
                )
        ).thenReturn(
                List.of(
                        atividade
                )
        );

        Map<String, Object> resultado =
                dashboardService.obterDashboard(
                        usuario,
                        1
                );

        List<?> atividades =
                (List<?>) resultado.get(
                        "atividades"
                );

        Map<?, ?> dados =
                (Map<?, ?>) atividades.get(0);

        assertEquals(
                "Disciplina",
                dados.get("disciplina")
        );
    }

    private void configurarDashboardComDisciplina() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "Semestre atual",
                        "S"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        "Java",
                        0,
                        "7.0",
                        "8.0"
                );

        when(
                semestreRepository.findByIdusuario(
                        10
                )
        ).thenReturn(
                List.of(
                        semestre
                )
        );

        when(
                disciplinaRepository.findByIdSemestre(
                        1
                )
        ).thenReturn(
                List.of(
                        disciplina
                )
        );
    }

    private Semestre criarSemestre(
            Integer idSemestre,
            Integer idCurso,
            String nome,
            String ativo) {

        Semestre semestre =
                new Semestre();

        semestre.setIdSemestre(
                idSemestre
        );

        semestre.setIdCurso(
                idCurso
        );

        semestre.setNome(
                nome
        );

        semestre.setAtivo(
                ativo
        );

        return semestre;
    }

    private Disciplina criarDisciplina(
            Integer idDisciplina,
            String nome,
            Integer faltas,
            String mediaMinima,
            String mediaFinal) {

        Disciplina disciplina =
                new Disciplina();

        disciplina.setIdDisciplina(
                idDisciplina
        );

        disciplina.setNome(
                nome
        );

        disciplina.setNomeProfessor(
                "Professor"
        );

        disciplina.setFaltas(
                faltas
        );

        if (mediaMinima != null) {

            disciplina.setMediaMinima(
                    new BigDecimal(
                            mediaMinima
                    )
            );
        }

        if (mediaFinal != null) {

            disciplina.setMediaFinal(
                    new BigDecimal(
                            mediaFinal
                    )
            );
        }

        return disciplina;
    }

    private Atividade criarAtividade(
            Integer idAtividade,
            Integer idDisciplina,
            String nome,
            String status,
            LocalDate dataEntrega) {

        Atividade atividade =
                new Atividade();

        atividade.setIdAtividade(
                idAtividade
        );

        atividade.setIdDisciplina(
                idDisciplina
        );

        atividade.setNome(
                nome
        );

        atividade.setTipo(
                "ATIVIDADE"
        );

        atividade.setDescricao(
                "Descrição"
        );

        atividade.setStatus(
                status
        );

        atividade.setDataDeEntrega(
                dataEntrega
        );

        atividade.setPrioridade(
                "MEDIA"
        );

        return atividade;
    }
}