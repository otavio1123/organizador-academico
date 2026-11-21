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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.organizador.api.model.Atividade;
import com.organizador.api.model.Curso;
import com.organizador.api.model.Disciplina;
import com.organizador.api.model.DocumentoAtividade;
import com.organizador.api.model.LogAuditoria;
import com.organizador.api.model.Semestre;
import com.organizador.api.model.Usuario;
import com.organizador.api.repository.AtividadeRepository;
import com.organizador.api.repository.CursoRepository;
import com.organizador.api.repository.DisciplinaRepository;
import com.organizador.api.repository.DocumentoAtividadeRepository;
import com.organizador.api.repository.LogAuditoriaRepository;
import com.organizador.api.repository.SemestreRepository;

@ExtendWith(MockitoExtension.class)
class AtividadeServiceTest {

    @Mock
    private AtividadeRepository atividadeRepository;

    @Mock
    private DocumentoAtividadeRepository documentoAtividadeRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private SemestreRepository semestreRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @Mock
    private Usuario usuario;

    @Mock
    private MultipartFile arquivo;

    @InjectMocks
    private AtividadeService atividadeService;

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
    void deveRetornarListaVaziaQuandoDisciplinaNaoPertenceAoUsuario() {

        when(
                disciplinaRepository.findById(1)
        ).thenReturn(
                Optional.empty()
        );

        List<Atividade> resultado =
                atividadeService.listarPorDisciplina(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );

        verify(
                atividadeRepository,
                never()
        ).findByIdDisciplina(
                1
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoSemestreNaoPertenceAoUsuario() {

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1
                );

        when(
                disciplinaRepository.findById(1)
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

        List<Atividade> resultado =
                atividadeService.listarPorDisciplina(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoCursoNaoPertenceAoUsuario() {

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        when(
                disciplinaRepository.findById(1)
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
                Optional.empty()
        );

        List<Atividade> resultado =
                atividadeService.listarPorDisciplina(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveListarAtividadesPorDisciplina() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Prova"
                );

        when(
                atividadeRepository.findByIdDisciplina(
                        1
                )
        ).thenReturn(
                List.of(
                        atividade
                )
        );

        List<Atividade> resultado =
                atividadeService.listarPorDisciplina(
                        1,
                        usuario
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertSame(
                atividade,
                resultado.get(0)
        );
    }

    @Test
    void deveRetornarVazioAoBuscarAtividadeInexistente() {

        when(
                atividadeRepository.findById(1)
        ).thenReturn(
                Optional.empty()
        );

        Optional<Atividade> resultado =
                atividadeService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveRetornarVazioQuandoAtividadeNaoPertenceAoUsuario() {

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Prova"
                );

        when(
                atividadeRepository.findById(1)
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                disciplinaRepository.findById(1)
        ).thenReturn(
                Optional.empty()
        );

        Optional<Atividade> resultado =
                atividadeService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveBuscarAtividadeDoUsuario() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Prova"
                );

        when(
                atividadeRepository.findById(1)
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        Optional<Atividade> resultado =
                atividadeService.buscarDoUsuario(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isPresent()
        );

        assertSame(
                atividade,
                resultado.get()
        );
    }

    @Test
    void naoDeveCadastrarAtividadeEmDisciplinaInexistente() {

        when(
                disciplinaRepository.findById(1)
        ).thenReturn(
                Optional.empty()
        );

        Atividade atividade =
                criarAtividade(
                        null,
                        null,
                        "Prova"
                );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "Disciplina não encontrada.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarAtividadeEmCursoTrancado() {

        configurarHierarquiaValida(
                "TRANCADO"
        );

        Atividade atividade =
                criarAtividade(
                        null,
                        null,
                        "Prova"
                );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "O curso está trancado. Os dados podem ser consultados, mas não alterados.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarAtividadeEmCursoConcluido() {

        configurarHierarquiaValida(
                "CONCLUIDO"
        );

        Atividade atividade =
                criarAtividade(
                        null,
                        null,
                        "Prova"
                );

        IllegalStateException erro =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "O curso está concluído. Os dados podem ser consultados, mas não alterados.",
                erro.getMessage()
        );
    }

    @Test
    void devePermitirAlteracaoQuandoStatusDoCursoForNull() {

        configurarHierarquiaValida(
                null
        );

        Atividade atividade =
                criarAtividade(
                        99,
                        null,
                        "Prova"
                );

        when(
                atividadeRepository.save(
                        atividade
                )
        ).thenReturn(
                atividade
        );

        Atividade resultado =
                atividadeService.cadastrar(
                        1,
                        atividade,
                        usuario
                );

        assertEquals(
                "Prova",
                resultado.getNome()
        );
    }

    @Test
    void naoDeveCadastrarAtividadeNula() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        null,
                                        usuario
                                )
                );

        assertEquals(
                "Os dados da atividade são obrigatórios.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarAtividadeSemNome() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                " "
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "O nome da atividade é obrigatório.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarTipoInvalido() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                "Prova"
        );

        when(
                atividade.getTipo()
        ).thenReturn(
                "INVALIDO"
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "Tipo de atividade inválido.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarStatusInvalido() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                "Prova"
        );

        when(
                atividade.getStatus()
        ).thenReturn(
                "INVALIDO"
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "Status da atividade inválido.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarNotaMaximaNegativa() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                "Prova"
        );

        when(
                atividade.getNotaMaxima()
        ).thenReturn(
                new BigDecimal("-1")
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "A nota máxima não pode ser negativa.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarNotaObtidaNegativa() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                "Prova"
        );

        when(
                atividade.getNotaMaxima()
        ).thenReturn(
                BigDecimal.TEN
        );

        when(
                atividade.getNotaObtida()
        ).thenReturn(
                new BigDecimal("-1")
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "A nota obtida não pode ser negativa.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarNotaObtidaSemNotaMaxima() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                "Prova"
        );

        when(
                atividade.getNotaObtida()
        ).thenReturn(
                new BigDecimal("8")
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "Informe a nota máxima antes da nota obtida.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarNotaObtidaMaiorQueNotaMaxima() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                "Prova"
        );

        when(
                atividade.getNotaMaxima()
        ).thenReturn(
                BigDecimal.TEN
        );

        when(
                atividade.getNotaObtida()
        ).thenReturn(
                new BigDecimal("11")
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "A nota obtida não pode ser maior que a nota máxima.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarPesoNegativo() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                org.mockito.Mockito.mock(
                        Atividade.class
                );

        when(
                atividade.getNome()
        ).thenReturn(
                "Trabalho"
        );

        when(
                atividade.getPeso()
        ).thenReturn(
                new BigDecimal("-1")
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.cadastrar(
                                        1,
                                        atividade,
                                        usuario
                                )
                );

        assertEquals(
                "O peso da atividade não pode ser negativo.",
                erro.getMessage()
        );
    }

    @Test
    void deveCadastrarAtividadeComValoresPadrao() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        99,
                        null,
                        "  Trabalho Final  "
                );

        atividade.setTipo(
                " "
        );

        atividade.setStatus(
                " "
        );

        when(
                atividadeRepository.save(
                        atividade
                )
        ).thenReturn(
                atividade
        );

        Atividade resultado =
                atividadeService.cadastrar(
                        1,
                        atividade,
                        usuario
                );

        assertNull(
                resultado.getIdAtividade()
        );

        assertEquals(
                1,
                resultado.getIdDisciplina()
        );

        assertEquals(
                "Trabalho Final",
                resultado.getNome()
        );

        assertNull(
                resultado.getTipo()
        );

        assertEquals(
                "PENDENTE",
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
                "Cadastrou a atividade Trabalho Final",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveNormalizarTipoEStatusAoCadastrar() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        null,
                        null,
                        "  Prova Final  "
                );

        atividade.setTipo(
                " prova "
        );

        atividade.setStatus(
                " em_andamento "
        );

        when(
                atividadeRepository.save(
                        atividade
                )
        ).thenReturn(
                atividade
        );

        Atividade resultado =
                atividadeService.cadastrar(
                        1,
                        atividade,
                        usuario
                );

        assertEquals(
                "Prova Final",
                resultado.getNome()
        );

        assertEquals(
                "PROVA",
                resultado.getTipo()
        );

        assertEquals(
                "EM_ANDAMENTO",
                resultado.getStatus()
        );
    }

    @Test
    void deveAtualizarAtividade() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho antigo"
                );

        Atividade novosDados =
                criarAtividade(
                        null,
                        null,
                        "  Prova Final  "
                );

        novosDados.setTipo(
                "prova"
        );

        novosDados.setStatus(
                "concluida"
        );

        novosDados.setNotaMaxima(
                BigDecimal.TEN
        );

        novosDados.setNotaObtida(
                new BigDecimal("9")
        );

        novosDados.setPeso(
                new BigDecimal("2")
        );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                atividadeRepository.save(
                        atividade
                )
        ).thenReturn(
                atividade
        );

        Atividade resultado =
                atividadeService.atualizar(
                        1,
                        novosDados,
                        usuario
                );

        assertEquals(
                "Prova Final",
                resultado.getNome()
        );

        assertEquals(
                "PROVA",
                resultado.getTipo()
        );

        assertEquals(
                "CONCLUIDA",
                resultado.getStatus()
        );

        assertEquals(
                BigDecimal.TEN,
                resultado.getNotaMaxima()
        );

        assertEquals(
                new BigDecimal("9"),
                resultado.getNotaObtida()
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
                "Editou a atividade Prova Final",
                captor.getValue().getAcao()
        );
    }

    @Test
    void deveLancarErroAoAtualizarAtividadeInexistente() {

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        Atividade novosDados =
                criarAtividade(
                        null,
                        null,
                        "Prova"
                );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.atualizar(
                                        1,
                                        novosDados,
                                        usuario
                                )
                );

        assertEquals(
                "Atividade não encontrada.",
                erro.getMessage()
        );
    }

    @Test
    void deveRetornarFalseAoExcluirAtividadeInexistente() {

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                atividadeService.excluir(
                        1,
                        usuario
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveExcluirAtividadeEDocumentos() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        DocumentoAtividade documento1 =
                criarDocumento(
                        1,
                        1,
                        "caminho/arquivo1.pdf"
                );

        DocumentoAtividade documento2 =
                criarDocumento(
                        2,
                        1,
                        "caminho/arquivo2.pdf"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                documentoAtividadeRepository.findByIdAtividade(
                        1
                )
        ).thenReturn(
                List.of(
                        documento1,
                        documento2
                )
        );

        boolean resultado =
                atividadeService.excluir(
                        1,
                        usuario
                );

        assertTrue(
                resultado
        );

        verify(
                supabaseStorageService
        ).excluirDocumentoAtividade(
                "caminho/arquivo1.pdf"
        );

        verify(
                supabaseStorageService
        ).excluirDocumentoAtividade(
                "caminho/arquivo2.pdf"
        );

        verify(
                documentoAtividadeRepository
        ).deleteAll(
                List.of(
                        documento1,
                        documento2
                )
        );

        verify(
                atividadeRepository
        ).delete(
                atividade
        );
    }

    @Test
    void deveRetornarListaVaziaDeDocumentosQuandoAtividadeNaoExiste() {

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.empty()
        );

        List<DocumentoAtividade> resultado =
                atividadeService.listarDocumentos(
                        1,
                        usuario
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveListarDocumentosDaAtividade() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        DocumentoAtividade documento =
                criarDocumento(
                        1,
                        1,
                        "arquivo.pdf"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                documentoAtividadeRepository.findByIdAtividade(
                        1
                )
        ).thenReturn(
                List.of(
                        documento
                )
        );

        List<DocumentoAtividade> resultado =
                atividadeService.listarDocumentos(
                        1,
                        usuario
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertSame(
                documento,
                resultado.get(0)
        );
    }

    @Test
    void deveAdicionarDocumento() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                arquivo.getOriginalFilename()
        ).thenReturn(
                "trabalho.pdf"
        );

        when(
                arquivo.getContentType()
        ).thenReturn(
                "application/pdf"
        );

        when(
                arquivo.getSize()
        ).thenReturn(
                1024L
        );

        when(
                supabaseStorageService.enviarDocumentoAtividade(
                        arquivo,
                        10,
                        1
                )
        ).thenReturn(
                "usuario-10/atividade-1/trabalho.pdf"
        );

        when(
                documentoAtividadeRepository.save(
                        any(
                                DocumentoAtividade.class
                        )
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        DocumentoAtividade resultado =
                atividadeService.adicionarDocumento(
                        1,
                        arquivo,
                        usuario
                );

        assertEquals(
                1,
                resultado.getIdAtividade()
        );

        assertEquals(
                "trabalho.pdf",
                resultado.getNomeOriginal()
        );

        assertEquals(
                "application/pdf",
                resultado.getTipoMime()
        );

        assertEquals(
                1024L,
                resultado.getTamanhoBytes()
        );

        assertEquals(
                "usuario-10/atividade-1/trabalho.pdf",
                resultado.getCaminhoStorage()
        );
    }

    @Test
    void deveExcluirArquivoDoStorageQuandoFalharSalvarDocumento() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                supabaseStorageService.enviarDocumentoAtividade(
                        arquivo,
                        10,
                        1
                )
        ).thenReturn(
                "caminho/documento.pdf"
        );

        when(
                documentoAtividadeRepository.save(
                        any(
                                DocumentoAtividade.class
                        )
                )
        ).thenThrow(
                new RuntimeException(
                        "Erro ao salvar documento"
                )
        );

        RuntimeException erro =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                atividadeService.adicionarDocumento(
                                        1,
                                        arquivo,
                                        usuario
                                )
                );

        assertEquals(
                "Erro ao salvar documento",
                erro.getMessage()
        );

        verify(
                supabaseStorageService
        ).excluirDocumentoAtividade(
                "caminho/documento.pdf"
        );
    }

    @Test
    void deveGerarUrlTemporariaDoDocumento() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        DocumentoAtividade documento =
                criarDocumento(
                        7,
                        1,
                        "caminho/documento.pdf"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                documentoAtividadeRepository
                        .findByIdDocumentoAndIdAtividade(
                                7,
                                1
                        )
        ).thenReturn(
                Optional.of(
                        documento
                )
        );

        when(
                supabaseStorageService
                        .gerarUrlTemporariaDocumento(
                                "caminho/documento.pdf"
                        )
        ).thenReturn(
                "https://exemplo.com/documento"
        );

        String resultado =
                atividadeService.gerarUrlDocumento(
                        1,
                        7,
                        usuario
                );

        assertEquals(
                "https://exemplo.com/documento",
                resultado
        );
    }

    @Test
    void deveLancarErroQuandoDocumentoNaoExiste() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                documentoAtividadeRepository
                        .findByIdDocumentoAndIdAtividade(
                                7,
                                1
                        )
        ).thenReturn(
                Optional.empty()
        );

        IllegalArgumentException erro =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                atividadeService.gerarUrlDocumento(
                                        1,
                                        7,
                                        usuario
                                )
                );

        assertEquals(
                "Documento não encontrado.",
                erro.getMessage()
        );
    }

    @Test
    void deveRetornarFalseAoExcluirDocumentoInexistente() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                documentoAtividadeRepository
                        .findByIdDocumentoAndIdAtividade(
                                7,
                                1
                        )
        ).thenReturn(
                Optional.empty()
        );

        boolean resultado =
                atividadeService.excluirDocumento(
                        1,
                        7,
                        usuario
                );

        assertFalse(
                resultado
        );
    }

    @Test
    void deveExcluirDocumento() {

        configurarHierarquiaValida(
                "ATIVO"
        );

        Atividade atividade =
                criarAtividade(
                        1,
                        1,
                        "Trabalho"
                );

        DocumentoAtividade documento =
                criarDocumento(
                        7,
                        1,
                        "caminho/documento.pdf"
                );

        when(
                atividadeRepository.findById(
                        1
                )
        ).thenReturn(
                Optional.of(
                        atividade
                )
        );

        when(
                documentoAtividadeRepository
                        .findByIdDocumentoAndIdAtividade(
                                7,
                                1
                        )
        ).thenReturn(
                Optional.of(
                        documento
                )
        );

        boolean resultado =
                atividadeService.excluirDocumento(
                        1,
                        7,
                        usuario
                );

        assertTrue(
                resultado
        );

        verify(
                documentoAtividadeRepository
        ).delete(
                documento
        );

        verify(
                supabaseStorageService
        ).excluirDocumentoAtividade(
                "caminho/documento.pdf"
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
                "Excluiu documento da atividade Trabalho",
                captor.getValue().getAcao()
        );
    }

    private void configurarHierarquiaValida(
            String statusCurso) {

        Disciplina disciplina =
                criarDisciplina(
                        1,
                        1
                );

        Semestre semestre =
                criarSemestre(
                        1,
                        1
                );

        Curso curso =
                criarCurso(
                        1,
                        statusCurso
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
    }

    private Atividade criarAtividade(
            Integer idAtividade,
            Integer idDisciplina,
            String nome) {

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

        atividade.setStatus(
                "PENDENTE"
        );

        return atividade;
    }

    private Disciplina criarDisciplina(
            Integer idDisciplina,
            Integer idSemestre) {

        Disciplina disciplina =
                new Disciplina();

        disciplina.setIdDisciplina(
                idDisciplina
        );

        disciplina.setIdSemestre(
                idSemestre
        );

        disciplina.setNome(
                "Java"
        );

        return disciplina;
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

    private DocumentoAtividade criarDocumento(
            Integer idDocumento,
            Integer idAtividade,
            String caminho) {

        DocumentoAtividade documento =
                new DocumentoAtividade();

        documento.setIdDocumento(
                idDocumento
        );

        documento.setIdAtividade(
                idAtividade
        );

        documento.setNomeOriginal(
                "documento.pdf"
        );

        documento.setCaminhoStorage(
                caminho
        );

        documento.setTipoMime(
                "application/pdf"
        );

        documento.setTamanhoBytes(
                1024L
        );

        return documento;
    }
}