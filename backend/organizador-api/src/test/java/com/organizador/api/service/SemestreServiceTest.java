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

import java.time.LocalDate;
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
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.CursoRepository;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.SemestreRepository;

@ExtendWith(MockitoExtension.class)
class SemestreServiceTest {

    @Mock
    private SemestreRepository semestreRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @Mock
    private Usuario usuario;

    @InjectMocks
    private SemestreService semestreService;

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
    void deveListarSemestresDoUsuario() {

        Semestre semestre1 =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
                );

        Semestre semestre2 =
                criarSemestre(
                        2,
                        1,
                        "2º Semestre",
                        "S"
                );

        List<Semestre> semestres =
                List.of(
                        semestre1,
                        semestre2
                );

        when(
                semestreRepository.findByIdusuario(
                        10
                )
        ).thenReturn(
                semestres
        );

        List<Semestre> resultado =
                semestreService.listar(
                        usuario
                );

        assertEquals(
                2,
                resultado.size()
        );

        assertSame(
                semestres,
                resultado
        );

        verify(
                semestreRepository
        ).findByIdusuario(
                10
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoCursoNaoPertenceAoUsuario() {

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        List<Semestre> resultado =
                semestreService.listarPorCurso(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );

        verify(
                semestreRepository,
                never()
        ).findByIdusuarioAndIdCurso(
                10,
                1
        );
    }

    @Test
    void deveListarSemestresPorCurso() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "S"
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
                semestreRepository.findByIdusuarioAndIdCurso(
                        10,
                        1
                )
        ).thenReturn(
                List.of(
                        semestre
                )
        );

        List<Semestre> resultado =
                semestreService.listarPorCurso(
                        1,
                        usuario
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertSame(
                semestre,
                resultado.get(0)
        );
    }

    @Test
    void cursoPertenceAoUsuarioDeveRetornarFalseQuandoIdCursoForNull() {

        boolean resultado =
                semestreService.cursoPertenceAoUsuario(
                        null,
                        usuario
                );

        assertFalse(
                resultado
        );

        verify(
                cursoRepository,
                never()
        ).findByIdCursoAndIdUsuario(
                any(),
                any()
        );
    }

    @Test
    void cursoPertenceAoUsuarioDeveRetornarTrueQuandoCursoExistir() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
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
                semestreService.cursoPertenceAoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado
        );
    }

    @Test
    void deveBuscarSemestreDoUsuario() {

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "S"
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

        Optional<Semestre> resultado =
                semestreService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isPresent()
        );

        assertSame(
                semestre,
                resultado.get()
        );
    }

    @Test
    void naoDeveCadastrarSemestreSemCurso() {

        Semestre semestre =
                criarSemestre(
                        null,
                        null,
                        "1º Semestre",
                        "N"
                );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                semestreService.cadastrar(
                                        semestre,
                                        usuario
                                )
                );

        assertEquals(
                "Selecione um curso.",
                erro.getMessage()
        );

        verify(
                semestreRepository,
                never()
        ).save(
                any()
        );
    }

    @Test
    void naoDeveCadastrarSemestreEmCursoInexistente() {

        Semestre semestre =
                criarSemestre(
                        null,
                        1,
                        "1º Semestre",
                        "N"
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
                                semestreService.cadastrar(
                                        semestre,
                                        usuario
                                )
                );

        assertEquals(
                "Curso não encontrado.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarSemestreEmCursoTrancado() {

        Curso curso =
                criarCurso(
                        1,
                        "TRANCADO"
                );

        Semestre semestre =
                criarSemestre(
                        null,
                        1,
                        "1º Semestre",
                        "N"
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
                                semestreService.cadastrar(
                                        semestre,
                                        usuario
                                )
                );

        assertEquals(
                "Não é possível alterar semestres de um curso trancado.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarSemestreEmCursoConcluido() {

        Curso curso =
                criarCurso(
                        1,
                        "CONCLUIDO"
                );

        Semestre semestre =
                criarSemestre(
                        null,
                        1,
                        "1º Semestre",
                        "N"
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
                                semestreService.cadastrar(
                                        semestre,
                                        usuario
                                )
                );

        assertEquals(
                "Não é possível alterar semestres de um curso concluído.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarSemestreComDataFinalAnteriorAInicial() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        null,
                        1,
                        "1º Semestre",
                        "N"
                );

        semestre.setDataInicio(
                LocalDate.of(
                        2026,
                        8,
                        1
                )
        );

        semestre.setDataFim(
                LocalDate.of(
                        2026,
                        7,
                        1
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

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                semestreService.cadastrar(
                                        semestre,
                                        usuario
                                )
                );

        assertEquals(
                "A data final não pode ser anterior à data inicial.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarSegundoSemestreAtivoNoMesmoCurso() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        null,
                        1,
                        "2º Semestre",
                        "S"
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
                semestreRepository.existsByIdCursoAndAtivo(
                        1,
                        "S"
                )
        ).thenReturn(
                true
        );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                semestreService.cadastrar(
                                        semestre,
                                        usuario
                                )
                );

        assertEquals(
                "Já existe um semestre ativo para este curso.",
                erro.getMessage()
        );
    }

    @Test
    void deveCadastrarSemestreComAtivoNaoQuandoNaoInformado() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        99,
                        1,
                        "1º Semestre",
                        null
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
                semestreRepository.save(
                        semestre
                )
        ).thenReturn(
                semestre
        );

        Semestre resultado =
                semestreService.cadastrar(
                        semestre,
                        usuario
                );

        assertNull(
                resultado.getIdSemestre()
        );

        assertEquals(
                10,
                resultado.getIdusuario()
        );

        assertEquals(
                "N",
                resultado.getAtivo()
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
                "Cadastrou o semestre 1º Semestre",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveRetornarNullAoAtualizarSemestreInexistente() {

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        Semestre novosDados =
                criarSemestre(
                        null,
                        1,
                        "1º Semestre",
                        "N"
                );

        Semestre resultado =
                semestreService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertNull(
                resultado
        );

        verify(
                semestreRepository,
                never()
        ).save(
                any()
        );
    }

    @Test
    void naoDeveAtualizarSemestreComDataFinalAnteriorAInicial() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
                );

        Semestre novosDados =
                criarSemestre(
                        null,
                        1,
                        "1º Semestre",
                        "N"
                );

        novosDados.setDataInicio(
                LocalDate.of(
                        2026,
                        8,
                        1
                )
        );

        novosDados.setDataFim(
                LocalDate.of(
                        2026,
                        7,
                        1
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

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                semestreService.atualizar(
                                        1,
                                        novosDados,
                                        usuario
                                )
                );

        assertEquals(
                "A data final não pode ser anterior à data inicial.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveAtualizarParaAtivoQuandoJaExisteOutroSemestreAtivo() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
                );

        Semestre outroSemestre =
                criarSemestre(
                        2,
                        1,
                        "2º Semestre",
                        "S"
                );

        Semestre novosDados =
                criarSemestre(
                        null,
                        1,
                        "1º Semestre",
                        "S"
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
                semestreRepository.findFirstByIdCursoAndAtivo(
                        1,
                        "S"
                )
        ).thenReturn(
                Optional.of(
                        outroSemestre
                )
        );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                semestreService.atualizar(
                                        1,
                                        novosDados,
                                        usuario
                                )
                );

        assertEquals(
                "Já existe um semestre ativo para este curso.",
                erro.getMessage()
        );
    }

    @Test
    void deveAtualizarSemestre() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
                );

        Semestre novosDados =
                criarSemestre(
                        null,
                        1,
                        "2º Semestre",
                        "S"
                );

        novosDados.setAno(
                2026
        );

        novosDados.setDataInicio(
                LocalDate.of(
                        2026,
                        7,
                        1
                )
        );

        novosDados.setDataFim(
                LocalDate.of(
                        2026,
                        12,
                        20
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
                semestreRepository.findFirstByIdCursoAndAtivo(
                        1,
                        "S"
                )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                semestreRepository.save(
                        semestre
                )
        ).thenReturn(
                semestre
        );

        Semestre resultado =
                semestreService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                "2º Semestre",
                resultado.getNome()
        );

        assertEquals(
                "S",
                resultado.getAtivo()
        );

        assertEquals(
                2026,
                resultado.getAno()
        );

        assertEquals(
                LocalDate.of(
                        2026,
                        7,
                        1
                ),
                resultado.getDataInicio()
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
                "Editou o semestre 2º Semestre",
                captor.getValue().getAcao()
        );
    }

    @Test
    void devePermitirMoverSemestreParaOutroCursoAtivo() {

        Curso cursoAtual =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Curso novoCurso =
                criarCurso(
                        2,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
                );

        Semestre novosDados =
                criarSemestre(
                        null,
                        2,
                        "1º Semestre",
                        "N"
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
                        cursoAtual
                )
        );

        when(
                cursoRepository.findByIdCursoAndIdUsuario(
                        2,
                        10
                )
        ).thenReturn(
                Optional.of(
                        novoCurso
                )
        );

        when(
                semestreRepository.save(
                        semestre
                )
        ).thenReturn(
                semestre
        );

        Semestre resultado =
                semestreService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                2,
                resultado.getIdCurso()
        );
    }

    @Test
    void deveRetornarFalseAoAtualizarAtivoDeSemestreInexistente() {

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                semestreService.atualizarAtivo(
                        1,
                        "S",
                        usuario
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void naoDeveAtivarSemestreQuandoJaExisteOutroAtivo() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
                );

        Semestre outroSemestre =
                criarSemestre(
                        2,
                        1,
                        "2º Semestre",
                        "S"
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
                semestreRepository.findFirstByIdCursoAndAtivo(
                        1,
                        "S"
                )
        ).thenReturn(
                Optional.of(
                        outroSemestre
                )
        );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                semestreService.atualizarAtivo(
                                        1,
                                        "S",
                                        usuario
                                )
                );

        assertEquals(
                "Já existe um semestre ativo para este curso.",
                erro.getMessage()
        );
    }

    @Test
    void deveAtivarSemestre() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
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
                semestreRepository.findFirstByIdCursoAndAtivo(
                        1,
                        "S"
                )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                semestreService.atualizarAtivo(
                        1,
                        "S",
                        usuario
                );

        assertTrue(
                resultado
        );

        assertEquals(
                "S",
                semestre.getAtivo()
        );

        verify(
                semestreRepository
        ).save(
                semestre
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
                "Ativou o semestre 1º Semestre",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveDesativarSemestre() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "S"
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
                semestreService.atualizarAtivo(
                        1,
                        "N",
                        usuario
                );

        assertTrue(
                resultado
        );

        assertEquals(
                "N",
                semestre.getAtivo()
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
                "Desativou o semestre 1º Semestre",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveRetornarFalseAoExcluirSemestreInexistente() {

        when(
                semestreRepository.findByIdSemestreAndIdusuario(
                        1,
                        10
                )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                semestreService.excluir(
                        1,
                        usuario
                );

        assertFalse(
                resultado
        );

        verify(
                semestreRepository,
                never()
        ).delete(
                any()
        );
    }

    @Test
    void deveExcluirSemestre() {

        Curso curso =
                criarCurso(
                        1,
                        "ATIVO"
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1,
                        "1º Semestre",
                        "N"
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
                semestreService.excluir(
                        1,
                        usuario
                );

        assertTrue(
                resultado
        );

        verify(
                semestreRepository
        ).delete(
                semestre
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
                "Excluiu o semestre 1º Semestre",
                captor.getValue().getAcao()
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

        semestre.setAno(
                2026
        );

        semestre.setDataInicio(
                LocalDate.of(
                        2026,
                        1,
                        1
                )
        );

        semestre.setDataFim(
                LocalDate.of(
                        2026,
                        6,
                        30
                )
        );

        semestre.setAtivo(
                ativo
        );

        return semestre;
    }
}