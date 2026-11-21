package com.organizador.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.organizador.api.model.Curso;
import com.organizador.api.model.Disciplina;
import com.organizador.api.model.HorarioDisciplina;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.CursoRepository;
import com.organizador.api.repository.DisciplinaRepository;
import com.organizador.api.repository.HorarioDisciplinaRepository;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.SemestreRepository;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private SemestreRepository semestreRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @Mock
    private HorarioDisciplinaRepository horarioDisciplinaRepository;

    @Mock
    private Usuario usuario;

    @InjectMocks
    private DisciplinaService disciplinaService;

    @BeforeEach
    void configurarUsuario() {

        lenient()
                .when(usuario.getId())
                .thenReturn(10);

        lenient()
                .when(usuario.getNome())
                .thenReturn("Otávio");
    }

    @Test
    void semestrePertenceAoUsuarioDeveRetornarTrue() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        boolean resultado =
                disciplinaService.semestrePertenceAoUsuario(
                        1,
                        10
                );

        assertTrue(
                resultado
        );
    }

    @Test
    void semestrePertenceAoUsuarioDeveRetornarFalse() {

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                disciplinaService.semestrePertenceAoUsuario(
                        1,
                        10
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoSemestreNaoPertenceAoUsuario() {

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        List<Disciplina> resultado =
                disciplinaService.listarPorSemestre(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );

        verify(
                disciplinaRepository,
                never()
        ).findByIdSemestre(
                1
        );
    }

    @Test
    void deveListarDisciplinasPorSemestre() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Banco de Dados"
                );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
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

        List<Disciplina> resultado =
                disciplinaService.listarPorSemestre(
                        1,
                        usuario
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertSame(
                disciplina,
                resultado.get(0)
        );
    }

    @Test
    void deveRetornarNullAoCadastrarEmSemestreDeOutroUsuario() {

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        Disciplina disciplina =
                criarDisciplina(
                        null,
                        null,
                        "Java"
                );

        Disciplina resultado =
                disciplinaService.cadastrar(
                        1,
                        disciplina,
                        usuario
                );

        assertNull(
                resultado
        );

        verify(
                disciplinaRepository,
                never()
        ).save(
                any()
        );
    }
@Test
void naoDeveCadastrarDisciplinaComFaltasNegativas() {

    Semestre semestre =
            criarSemestre(
                    1,
                    1
            );

    Curso curso =
            criarCurso(
                    1,
                    "ATIVO"
            );

    Disciplina disciplina =
            org.mockito.Mockito.mock(
                    Disciplina.class
            );

    when(
            disciplina.getFaltas()
    ).thenReturn(
            -1
    );

    when(
            semestreRepository.findByIdSemestreAndIdusuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    semestre
            )
    );

    when(
            cursoRepository.findByIdCursoAndIdUsuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    curso
            )
    );

    Disciplina resultado =
            disciplinaService.cadastrar(
                    1,
                    disciplina,
                    usuario
            );

    assertNull(
            resultado
    );

    verify(
            disciplinaRepository,
            never()
    ).save(
            any()
    );
}

   @Test
void naoDeveCadastrarDisciplinaComLimiteDeFaltasInvalido() {

    Semestre semestre =
            criarSemestre(
                    1,
                    1
            );

    Curso curso =
            criarCurso(
                    1,
                    "ATIVO"
            );

    Disciplina disciplina =
            org.mockito.Mockito.mock(
                    Disciplina.class
            );

    when(
            disciplina.getFaltas()
    ).thenReturn(
            0
    );

    when(
            disciplina.getLimiteFaltas()
    ).thenReturn(
            0
    );

    when(
            semestreRepository.findByIdSemestreAndIdusuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    semestre
            )
    );

    when(
            cursoRepository.findByIdCursoAndIdUsuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    curso
            )
    );

    Disciplina resultado =
            disciplinaService.cadastrar(
                    1,
                    disciplina,
                    usuario
            );

    assertNull(
            resultado
    );

    verify(
            disciplinaRepository,
            never()
    ).save(
            any()
    );
}

    @Test
    void deveCadastrarDisciplinaComZeroFaltasQuandoNaoInformado() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        99,
                        null,
                        "Java"
                );

        disciplina.setFaltas(
                null
        );

        disciplina.setLimiteFaltas(
                20
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                disciplinaRepository.save(
                        disciplina
                )
        ).thenReturn(
                disciplina
        );

        Disciplina resultado =
                disciplinaService.cadastrar(
                        1,
                        disciplina,
                        usuario
                );

        assertNull(
                resultado.getIdDisciplina()
        );

        assertEquals(
                1,
                resultado.getIdSemestre()
        );

        assertEquals(
                0,
                resultado.getFaltas()
        );

        ArgumentCaptor<LogAuditoria> captor =
                ArgumentCaptor.forClass(
                        LogAuditoria.class
                );

        verify(
                logAuditoriaRepository
        ).save(
                captor.capture()
        );

        assertEquals(
                "Cadastrou a disciplina Java",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveBuscarDisciplinaDoUsuario() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        Optional<Disciplina> resultado =
                disciplinaService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isPresent()
        );

        assertSame(
                disciplina,
                resultado.get()
        );
    }

    @Test
    void deveRetornarVazioAoBuscarDisciplinaInexistente() {

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        Optional<Disciplina> resultado =
                disciplinaService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveRetornarVazioQuandoDisciplinaNaoPertenceAoUsuario() {

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        Optional<Disciplina> resultado =
                disciplinaService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveRetornarNullAoAtualizarDisciplinaInexistente() {

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        Disciplina novosDados =
                criarDisciplina(
                        null,
                        null,
                        "Nova disciplina"
                );

        Disciplina resultado =
                disciplinaService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertNull(
                resultado
        );
    }

 @Test
void naoDeveAtualizarComFaltasNegativas() {

    Semestre semestre =
            criarSemestre(
                    1,
                    1
            );

    Curso curso =
            criarCurso(
                    1,
                    "ATIVO"
            );

    Disciplina disciplina =
            criarDisciplina(
                    1,
                    1,
                    "Java"
            );

    Disciplina novosDados =
            org.mockito.Mockito.mock(
                    Disciplina.class
            );

    when(
            novosDados.getFaltas()
    ).thenReturn(
            -1
    );

    when(
            disciplinaRepository.findById(
                    1
            )
    ).thenReturn(
            Optional.of(
                    disciplina
            )
    );

    when(
            semestreRepository.findByIdSemestreAndIdusuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    semestre
            )
    );

    when(
            cursoRepository.findByIdCursoAndIdUsuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    curso
            )
    );

    Disciplina resultado =
            disciplinaService.atualizar(
                    1,
                    novosDados,
                    usuario
            );

    assertNull(
            resultado
    );

    verify(
            disciplinaRepository,
            never()
    ).save(
            any()
    );
}
 @Test
void naoDeveAtualizarComLimiteFaltasInvalido() {

    Semestre semestre =
            criarSemestre(
                    1,
                    1
            );

    Curso curso =
            criarCurso(
                    1,
                    "ATIVO"
            );

    Disciplina disciplina =
            criarDisciplina(
                    1,
                    1,
                    "Java"
            );

    Disciplina novosDados =
            org.mockito.Mockito.mock(
                    Disciplina.class
            );

    when(
            novosDados.getFaltas()
    ).thenReturn(
            0
    );

    when(
            novosDados.getLimiteFaltas()
    ).thenReturn(
            0
    );

    when(
            disciplinaRepository.findById(
                    1
            )
    ).thenReturn(
            Optional.of(
                    disciplina
            )
    );

    when(
            semestreRepository.findByIdSemestreAndIdusuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    semestre
            )
    );

    when(
            cursoRepository.findByIdCursoAndIdUsuario(
                    1,
                    10
            )
    ).thenReturn(
            Optional.of(
                    curso
            )
    );

    Disciplina resultado =
            disciplinaService.atualizar(
                    1,
                    novosDados,
                    usuario
            );

    assertNull(
            resultado
    );

    verify(
            disciplinaRepository,
            never()
    ).save(
            any()
    );
}
    @Test
    void deveAtualizarDisciplina() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        Disciplina novosDados =
                criarDisciplina(
                        null,
                        null,
                        "Java Avançado"
                );

        novosDados.setNomeProfessor(
                "Professor"
        );

        novosDados.setFaltas(
                null
        );

        novosDados.setLimiteFaltas(
                20
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                disciplinaRepository.save(
                        disciplina
                )
        ).thenReturn(
                disciplina
        );

        Disciplina resultado =
                disciplinaService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                "Java Avançado",
                resultado.getNome()
        );

        assertEquals(
                "Professor",
                resultado.getNomeProfessor()
        );

        assertEquals(
                0,
                resultado.getFaltas()
        );

        assertEquals(
                20,
                resultado.getLimiteFaltas()
        );

        ArgumentCaptor<LogAuditoria> captor =
                ArgumentCaptor.forClass(
                        LogAuditoria.class
                );

        verify(
                logAuditoriaRepository
        ).save(
                captor.capture()
        );

        assertEquals(
                "Editou a disciplina Java Avançado",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveAdicionarFalta() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        disciplina.setFaltas(
                2
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                disciplinaRepository.save(
                        disciplina
                )
        ).thenReturn(
                disciplina
        );

        Disciplina resultado =
                disciplinaService.adicionarFalta(
                        1,
                        usuario
                );

        assertEquals(
                3,
                resultado.getFaltas()
        );
    }

    @Test
    void deveAdicionarPrimeiraFaltaQuandoFaltasForNull() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        disciplina.setFaltas(
                null
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                disciplinaRepository.save(
                        disciplina
                )
        ).thenReturn(
                disciplina
        );

        Disciplina resultado =
                disciplinaService.adicionarFalta(
                        1,
                        usuario
                );

        assertEquals(
                1,
                resultado.getFaltas()
        );
    }

    @Test
    void deveRetornarNullAoAdicionarFaltaEmDisciplinaInexistente() {

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        Disciplina resultado =
                disciplinaService.adicionarFalta(
                        1,
                        usuario
                );

        assertNull(
                resultado
        );
    }

    @Test
    void deveRemoverFalta() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        disciplina.setFaltas(
                2
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                disciplinaRepository.save(
                        disciplina
                )
        ).thenReturn(
                disciplina
        );

        Disciplina resultado =
                disciplinaService.removerFalta(
                        1,
                        usuario
                );

        assertEquals(
                1,
                resultado.getFaltas()
        );
    }

    @Test
    void naoDeveRemoverFaltaQuandoQuantidadeForZero() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        disciplina.setFaltas(
                0
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        Disciplina resultado =
                disciplinaService.removerFalta(
                        1,
                        usuario
                );

        assertNull(
                resultado
        );
    }

    @Test
    void deveListarHorariosDaDisciplina() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        HorarioDisciplina horario =
                criarHorario();

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                horarioDisciplinaRepository.findByIdDisciplina(
                        1
                )
        ).thenReturn(
                List.of(
                        horario
                )
        );

        List<HorarioDisciplina> resultado =
                disciplinaService.listarHorarios(
                        1,
                        usuario
                );

        assertEquals(
                1,
                resultado.size()
        );
    }

    @Test
    void deveRetornarListaVaziaDeHorariosQuandoDisciplinaNaoExiste() {

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        List<HorarioDisciplina> resultado =
                disciplinaService.listarHorarios(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void naoDeveCadastrarHorarioSemDiaDaSemana() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        HorarioDisciplina horario =
                criarHorario();

        horario.setDiaSemana(
                " "
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        HorarioDisciplina resultado =
                disciplinaService.cadastrarHorario(
                        1,
                        horario,
                        usuario
                );

        assertNull(
                resultado
        );
    }

    @Test
    void naoDeveCadastrarHorarioComHoraFinalAnteriorOuIgualAInicial() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        HorarioDisciplina horario =
                criarHorario();

        horario.setHoraInicio(
                LocalTime.of(
                        20,
                        0
                )
        );

        horario.setHoraFim(
                LocalTime.of(
                        19,
                        0
                )
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        HorarioDisciplina resultado =
                disciplinaService.cadastrarHorario(
                        1,
                        horario,
                        usuario
                );

        assertNull(
                resultado
        );
    }

    @Test
    void deveCadastrarHorario() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        HorarioDisciplina horario =
                criarHorario();

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                horarioDisciplinaRepository.save(
                        horario
                )
        ).thenReturn(
                horario
        );

        HorarioDisciplina resultado =
                disciplinaService.cadastrarHorario(
                        1,
                        horario,
                        usuario
                );

        assertEquals(
                1,
                resultado.getIdDisciplina()
        );

        verify(
                horarioDisciplinaRepository
        ).save(
                horario
        );
    }

    @Test
    void deveRetornarFalseAoExcluirHorarioInexistente() {

        when(
                horarioDisciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                disciplinaService.excluirHorario(
                        1,
                        usuario
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveExcluirHorario() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        HorarioDisciplina horario =
                criarHorario();

        horario.setIdDisciplina(
                1
        );

        when(
                horarioDisciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        horario
                )
        );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        boolean resultado =
                disciplinaService.excluirHorario(
                        1,
                        usuario
                );

        assertTrue(
                resultado
        );

        verify(
                horarioDisciplinaRepository
        ).delete(
                horario
        );
    }

    @Test
    void deveRetornarFalseAoExcluirDisciplinaInexistente() {

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                disciplinaService.excluir(
                        1,
                        usuario
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveExcluirDisciplinaESeusHorarios() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1,
                        "Java"
                );

        when(
                disciplinaRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        disciplina
                )
        );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        boolean resultado =
                disciplinaService.excluir(
                        1,
                        usuario
                );

        assertTrue(
                resultado
        );

        verify(
                horarioDisciplinaRepository
        ).deleteByIdDisciplina(
                1
        );

        verify(
                disciplinaRepository
        ).delete(
                disciplina
        );
    }

    @Test
    void naoDeveAlterarDisciplinaDeCursoTrancado() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "TRANCADO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        null,
                        null,
                        "Java"
                );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                disciplinaService.cadastrar(
                                        1,
                                        disciplina,
                                        usuario
                                )
                );

        assertEquals(
                "Não é possível alterar disciplinas de um curso trancado.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveAlterarDisciplinaDeCursoConcluido() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        "CONCLUIDO"
                );

        Disciplina disciplina =
                criarDisciplina(
                        null,
                        null,
                        "Java"
                );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                disciplinaService.cadastrar(
                                        1,
                                        disciplina,
                                        usuario
                                )
                );

        assertEquals(
                "Não é possível alterar disciplinas de um curso concluído.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveAlterarQuandoCursoNaoForEncontrado() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Disciplina disciplina =
                criarDisciplina(
                        null,
                        null,
                        "Java"
                );

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                disciplinaService.cadastrar(
                                        1,
                                        disciplina,
                                        usuario
                                )
                );

        assertEquals(
                "Curso não encontrado.",
                erro.getMessage()
        );
    }

    private Curso criarCurso(
            Integer idCurso,
            String status) {

        Curso curso =
                new Curso();

        curso.setIdCurso(
                idCurso
        );

        curso.setNome(
                "Sistemas de Informação"
        );

        curso.setStatus(
                status
        );

        return curso;
    }

    private Semestre criarSemestre(
            Integer idSemestre,
            Integer idCurso) {

        Semestre semestre =
                new Semestre();

        semestre.setIdSemestre(
                idSemestre
        );

        semestre.setIdCurso(
                idCurso
        );

        semestre.setNome(
                "1º Semestre"
        );

        return semestre;
    }

    private Disciplina criarDisciplina(
            Integer idDisciplina,
            Integer idSemestre,
            String nome) {

        Disciplina disciplina =
                new Disciplina();

        disciplina.setIdDisciplina(
                idDisciplina
        );

        disciplina.setIdSemestre(
                idSemestre
        );

        disciplina.setNome(
                nome
        );

        disciplina.setFaltas(
                0
        );

        disciplina.setLimiteFaltas(
                20
        );

        return disciplina;
    }

    private HorarioDisciplina criarHorario() {

        HorarioDisciplina horario =
                new HorarioDisciplina();

        horario.setDiaSemana(
                "SEGUNDA"
        );

        horario.setHoraInicio(
                LocalTime.of(
                        19,
                        0
                )
        );

        horario.setHoraFim(
                LocalTime.of(
                        20,
                        40
                )
        );

        return horario;
    }
}