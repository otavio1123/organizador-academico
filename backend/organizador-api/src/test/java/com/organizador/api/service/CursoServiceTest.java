package com.organizador.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private SemestreRepository semestreRepository;

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @Mock
    private Usuario usuario;

    @InjectMocks
    private CursoService cursoService;
@BeforeEach
void configurarUsuario() {

    when(usuario.getId())
            .thenReturn(10);

    lenient()
            .when(usuario.getNome())
            .thenReturn("Otávio");
}
    @Test
    void deveListarCursosDoUsuario() {

        Curso curso1 =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        Curso curso2 =
                criarCurso(
                        2,
                        "Engenharia de Software",
                        "CONCLUIDO"
                );

        List<Curso> cursos =
                List.of(
                        curso1,
                        curso2
                );

        when(
                cursoRepository
                        .findByIdUsuario(10)
        ).thenReturn(
                cursos
        );

        List<Curso> resultado =
                cursoService.listar(
                        usuario
                );

        assertEquals(
                2,
                resultado.size()
        );

        assertSame(
                cursos,
                resultado
        );

        verify(
                cursoRepository
        ).findByIdUsuario(
                10
        );
    }

    @Test
    void deveCadastrarCursoComStatusAtivoQuandoStatusNaoInformado() {

        Curso curso =
                criarCurso(
                        99,
                        "Sistemas de Informação",
                        null
                );

        when(
                cursoRepository.save(
                        curso
                )
        ).thenReturn(
                curso
        );

        Curso resultado =
                cursoService.cadastrar(
                        curso,
                        usuario
                );

        assertNull(
                resultado.getIdCurso()
        );

        assertEquals(
                10,
                resultado.getIdUsuario()
        );

        assertEquals(
                "ATIVO",
                resultado.getStatus()
        );

        verify(
                cursoRepository
        ).save(
                curso
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
                "Cadastrou o curso Sistemas de Informação",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveRejeitarCadastroComStatusInvalido() {

        Curso curso =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "INVALIDO"
                );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                cursoService.cadastrar(
                                        curso,
                                        usuario
                                )
                );

        assertEquals(
                "Status do curso inválido.",
                erro.getMessage()
        );

        verify(
                cursoRepository,
                never()
        ).save(
                curso
        );
    }

    @Test
    void deveBuscarCursoDoUsuario() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        Optional<Curso> resultado =
                cursoService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isPresent()
        );

        assertSame(
                curso,
                resultado.get()
        );
    }

    @Test
    void deveRetornarNullAoAtualizarCursoInexistente() {

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.empty()
        );

        Curso novosDados =
                criarCurso(
                        null,
                        "Novo nome",
                        "ATIVO"
                );

        Curso resultado =
                cursoService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertNull(
                resultado
        );

        verify(
                cursoRepository,
                never()
        ).save(
                novosDados
        );
    }

    @Test
    void deveEditarCursoMantendoStatusQuandoNovoStatusNaoInformado() {

        Curso curso =
                criarCurso(
                        1,
                        "Curso antigo",
                        "ATIVO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Curso atualizado",
                        ""
                );

        novosDados.setInstituicao(
                "UMC"
        );

        novosDados.setTipo(
                "Bacharelado"
        );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                cursoRepository.save(
                        curso
                )
        ).thenReturn(
                curso
        );

        Curso resultado =
                cursoService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                "Curso atualizado",
                resultado.getNome()
        );

        assertEquals(
                "UMC",
                resultado.getInstituicao()
        );

        assertEquals(
                "Bacharelado",
                resultado.getTipo()
        );

        assertEquals(
                "ATIVO",
                resultado.getStatus()
        );

        verify(
                semestreRepository,
                never()
        ).findFirstByIdCursoAndAtivo(
                1,
                "S"
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
                "Editou o curso Curso atualizado",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveTrancarCursoEDesativarSemestreAtivo() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "TRANCADO"
                );

        Semestre semestre =
                new Semestre();

        semestre.setAtivo(
                "S"
        );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                semestreRepository
                        .findFirstByIdCursoAndAtivo(
                                1,
                                "S"
                        )
        ).thenReturn(
                Optional.of(
                        semestre
                )
        );

        when(
                cursoRepository.save(
                        curso
                )
        ).thenReturn(
                curso
        );

        Curso resultado =
                cursoService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                "TRANCADO",
                resultado.getStatus()
        );

        assertEquals(
                "N",
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
                "Trancou o curso Sistemas de Informação",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveConcluirCursoMesmoSemSemestreAtivo() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "CONCLUIDO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                semestreRepository
                        .findFirstByIdCursoAndAtivo(
                                1,
                                "S"
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(
                cursoRepository.save(
                        curso
                )
        ).thenReturn(
                curso
        );

        Curso resultado =
                cursoService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                "CONCLUIDO",
                resultado.getStatus()
        );

        verify(
                semestreRepository,
                never()
        ).save(
                org.mockito.ArgumentMatchers.any(
                        Semestre.class
                )
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
                "Concluiu o curso Sistemas de Informação",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveReativarCursoTrancado() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "TRANCADO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                cursoRepository.save(
                        curso
                )
        ).thenReturn(
                curso
        );

        Curso resultado =
                cursoService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                "ATIVO",
                resultado.getStatus()
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
                "Reativou o curso Sistemas de Informação",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveReabrirCursoConcluido() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "CONCLUIDO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        when(
                cursoRepository.save(
                        curso
                )
        ).thenReturn(
                curso
        );

        cursoService.atualizar(
                1,
                novosDados,
                usuario
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
                "Reabriu o curso Sistemas de Informação",
                captor.getValue().getAcao()
        );
    }

    @Test
    void naoDevePermitirCursoTrancadoSerConcluidoDiretamente() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "TRANCADO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "CONCLUIDO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
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
                                cursoService.atualizar(
                                        1,
                                        novosDados,
                                        usuario
                                )
                );

        assertEquals(
                "Reative o curso antes de marcá-lo como concluído.",
                erro.getMessage()
        );

        verify(
                cursoRepository,
                never()
        ).save(
                curso
        );
    }

    @Test
    void naoDevePermitirCursoConcluidoSerTrancadoDiretamente() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "CONCLUIDO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "TRANCADO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
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
                                cursoService.atualizar(
                                        1,
                                        novosDados,
                                        usuario
                                )
                );

        assertEquals(
                "Reabra o curso antes de marcá-lo como trancado.",
                erro.getMessage()
        );
    }

    @Test
    void deveRejeitarAtualizacaoComStatusInvalido() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        Curso novosDados =
                criarCurso(
                        null,
                        "Sistemas de Informação",
                        "INVALIDO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
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
                                cursoService.atualizar(
                                        1,
                                        novosDados,
                                        usuario
                                )
                );

        assertEquals(
                "Status do curso inválido.",
                erro.getMessage()
        );
    }

    @Test
    void deveExcluirCursoDoUsuario() {

        Curso curso =
                criarCurso(
                        1,
                        "Sistemas de Informação",
                        "ATIVO"
                );

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.of(
                        curso
                )
        );

        boolean resultado =
                cursoService.excluir(
                        1,
                        usuario
                );

        assertTrue(
                resultado
        );

        verify(
                cursoRepository
        ).delete(
                curso
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
                "Excluiu o curso Sistemas de Informação",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveRetornarFalseAoExcluirCursoInexistente() {

        when(
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                1,
                                10
                        )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                cursoService.excluir(
                        1,
                        usuario
                );

        assertFalse(
                resultado
        );

        verify(
                cursoRepository,
                never()
        ).delete(
                org.mockito.ArgumentMatchers.any(
                        Curso.class
                )
        );
    }

    private Curso criarCurso(
            Integer idCurso,
            String nome,
            String status) {

        Curso curso =
                new Curso();

        curso.setIdCurso(
                idCurso
        );

        curso.setNome(
                nome
        );

        curso.setStatus(
                status
        );

        curso.setInstituicao(
                "UMC"
        );

        curso.setTipo(
                "Bacharelado"
        );

        return curso;
    }
}