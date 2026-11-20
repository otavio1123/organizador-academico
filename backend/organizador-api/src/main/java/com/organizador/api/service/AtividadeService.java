package com.organizador.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
public class AtividadeService {

private static final Set<String> TIPOS_PERMITIDOS =
        Set.of(
                "ATIVIDADE",
                "PROVA",
                "TRABALHO",
                "OUTRA"
        );

    private static final Set<String> STATUS_PERMITIDOS =
            Set.of(
                    "PENDENTE",
                    "EM_ANDAMENTO",
                    "CONCLUIDA"
            );

    private final AtividadeRepository atividadeRepository;
    private final DocumentoAtividadeRepository documentoAtividadeRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final SemestreRepository semestreRepository;
    private final CursoRepository cursoRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final SupabaseStorageService supabaseStorageService;

    public AtividadeService(
            AtividadeRepository atividadeRepository,
            DocumentoAtividadeRepository documentoAtividadeRepository,
            DisciplinaRepository disciplinaRepository,
            SemestreRepository semestreRepository,
            CursoRepository cursoRepository,
            LogAuditoriaRepository logAuditoriaRepository,
            SupabaseStorageService supabaseStorageService) {

        this.atividadeRepository = atividadeRepository;
        this.documentoAtividadeRepository = documentoAtividadeRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.semestreRepository = semestreRepository;
        this.cursoRepository = cursoRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    public List<Atividade> listarPorDisciplina(
            Integer idDisciplina,
            Usuario usuario) {

        Optional<Disciplina> disciplina =
                buscarDisciplinaDoUsuario(
                        idDisciplina,
                        usuario
                );

        if (disciplina.isEmpty()) {
            return List.of();
        }

        return atividadeRepository
                .findByIdDisciplina(
                        idDisciplina
                );
    }

    public Optional<Atividade> buscarDoUsuario(
            Integer idAtividade,
            Usuario usuario) {

        Optional<Atividade> atividadeEncontrada =
                atividadeRepository
                        .findById(
                                idAtividade
                        );

        if (atividadeEncontrada.isEmpty()) {
            return Optional.empty();
        }

        Atividade atividade =
                atividadeEncontrada.get();

        Optional<Disciplina> disciplina =
                buscarDisciplinaDoUsuario(
                        atividade.getIdDisciplina(),
                        usuario
                );

        if (disciplina.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                atividade
        );
    }

    public Atividade cadastrar(
            Integer idDisciplina,
            Atividade atividade,
            Usuario usuario) {

        Disciplina disciplina =
                obterDisciplinaDoUsuario(
                        idDisciplina,
                        usuario
                );

        validarCursoPermiteAlteracao(
                disciplina,
                usuario
        );

        validarDadosAtividade(
                atividade
        );

        atividade.setIdAtividade(
                null
        );

        atividade.setIdDisciplina(
                idDisciplina
        );

        prepararDados(
                atividade
        );

        Atividade atividadeSalva =
                atividadeRepository.save(
                        atividade
                );

        registrarAuditoria(
                usuario,
                "Cadastrou a atividade "
                        + atividadeSalva.getNome()
        );

        return atividadeSalva;
    }

    public Atividade atualizar(
            Integer idAtividade,
            Atividade novosDados,
            Usuario usuario) {

        Atividade atividade =
                obterAtividadeDoUsuario(
                        idAtividade,
                        usuario
                );

        Disciplina disciplina =
                obterDisciplinaDoUsuario(
                        atividade.getIdDisciplina(),
                        usuario
                );

        validarCursoPermiteAlteracao(
                disciplina,
                usuario
        );

        validarDadosAtividade(
                novosDados
        );

        prepararDados(
                novosDados
        );

        atividade.setNome(
                novosDados.getNome()
        );

        atividade.setTipo(
                novosDados.getTipo()
        );

        atividade.setDescricao(
                novosDados.getDescricao()
        );

        atividade.setDataDeEntrega(
                novosDados.getDataDeEntrega()
        );

        atividade.setStatus(
                novosDados.getStatus()
        );

        atividade.setNotaMaxima(
                novosDados.getNotaMaxima()
        );

        atividade.setNotaObtida(
                novosDados.getNotaObtida()
        );

        atividade.setPeso(
                novosDados.getPeso()
        );

        atividade.setPrioridade(
                novosDados.getPrioridade()
        );

        atividade.setObservacoes(
                novosDados.getObservacoes()
        );

        Atividade atividadeAtualizada =
                atividadeRepository.save(
                        atividade
                );

        registrarAuditoria(
                usuario,
                "Editou a atividade "
                        + atividadeAtualizada.getNome()
        );

        return atividadeAtualizada;
    }

    @Transactional
    public boolean excluir(
            Integer idAtividade,
            Usuario usuario) {

        Optional<Atividade> atividadeEncontrada =
                buscarDoUsuario(
                        idAtividade,
                        usuario
                );

        if (atividadeEncontrada.isEmpty()) {
            return false;
        }

        Atividade atividade =
                atividadeEncontrada.get();

        Disciplina disciplina =
                obterDisciplinaDoUsuario(
                        atividade.getIdDisciplina(),
                        usuario
                );

        validarCursoPermiteAlteracao(
                disciplina,
                usuario
        );

        String nomeAtividade =
                atividade.getNome();

        List<DocumentoAtividade> documentos =
                documentoAtividadeRepository
                        .findByIdAtividade(
                                idAtividade
                        );

        for (DocumentoAtividade documento
                : documentos) {

            supabaseStorageService
                    .excluirDocumentoAtividade(
                            documento.getCaminhoStorage()
                    );
        }

        documentoAtividadeRepository
                .deleteAll(
                        documentos
                );

        atividadeRepository.delete(
                atividade
        );

        registrarAuditoria(
                usuario,
                "Excluiu a atividade "
                        + nomeAtividade
        );

        return true;
    }

    public List<DocumentoAtividade> listarDocumentos(
            Integer idAtividade,
            Usuario usuario) {

        Optional<Atividade> atividade =
                buscarDoUsuario(
                        idAtividade,
                        usuario
                );

        if (atividade.isEmpty()) {
            return List.of();
        }

        return documentoAtividadeRepository
                .findByIdAtividade(
                        idAtividade
                );
    }

    public DocumentoAtividade adicionarDocumento(
            Integer idAtividade,
            MultipartFile arquivo,
            Usuario usuario) {

        Atividade atividade =
                obterAtividadeDoUsuario(
                        idAtividade,
                        usuario
                );

        Disciplina disciplina =
                obterDisciplinaDoUsuario(
                        atividade.getIdDisciplina(),
                        usuario
                );

        validarCursoPermiteAlteracao(
                disciplina,
                usuario
        );

        String caminhoStorage =
                supabaseStorageService
                        .enviarDocumentoAtividade(
                                arquivo,
                                usuario.getId(),
                                idAtividade
                        );

        try {

            DocumentoAtividade documento =
                    new DocumentoAtividade();

            documento.setIdAtividade(
                    idAtividade
            );

            documento.setNomeOriginal(
                    arquivo.getOriginalFilename()
            );

            documento.setCaminhoStorage(
                    caminhoStorage
            );

            documento.setTipoMime(
                    arquivo.getContentType()
            );

            documento.setTamanhoBytes(
                    arquivo.getSize()
            );

            documento.setDataUpload(
                    LocalDateTime.now()
            );

            DocumentoAtividade documentoSalvo =
                    documentoAtividadeRepository
                            .save(
                                    documento
                            );

            registrarAuditoria(
                    usuario,
                    "Adicionou documento à atividade "
                            + atividade.getNome()
            );

            return documentoSalvo;

        } catch (RuntimeException erro) {

            try {

                supabaseStorageService
                        .excluirDocumentoAtividade(
                                caminhoStorage
                        );

            } catch (RuntimeException erroExclusao) {

                erro.addSuppressed(
                        erroExclusao
                );
            }

            throw erro;
        }
    }

    public String gerarUrlDocumento(
            Integer idAtividade,
            Integer idDocumento,
            Usuario usuario) {

        DocumentoAtividade documento =
                obterDocumentoDoUsuario(
                        idAtividade,
                        idDocumento,
                        usuario
                );

        return supabaseStorageService
                .gerarUrlTemporariaDocumento(
                        documento.getCaminhoStorage()
                );
    }

    @Transactional
    public boolean excluirDocumento(
            Integer idAtividade,
            Integer idDocumento,
            Usuario usuario) {

        Atividade atividade =
                obterAtividadeDoUsuario(
                        idAtividade,
                        usuario
                );

        Disciplina disciplina =
                obterDisciplinaDoUsuario(
                        atividade.getIdDisciplina(),
                        usuario
                );

        validarCursoPermiteAlteracao(
                disciplina,
                usuario
        );

        Optional<DocumentoAtividade> documentoEncontrado =
                documentoAtividadeRepository
                        .findByIdDocumentoAndIdAtividade(
                                idDocumento,
                                idAtividade
                        );

        if (documentoEncontrado.isEmpty()) {
            return false;
        }

        DocumentoAtividade documento =
                documentoEncontrado.get();

        documentoAtividadeRepository.delete(
                documento
        );

        supabaseStorageService
                .excluirDocumentoAtividade(
                        documento.getCaminhoStorage()
                );

        registrarAuditoria(
                usuario,
                "Excluiu documento da atividade "
                        + atividade.getNome()
        );

        return true;
    }

    private Optional<Disciplina> buscarDisciplinaDoUsuario(
            Integer idDisciplina,
            Usuario usuario) {

        Optional<Disciplina> disciplinaEncontrada =
                disciplinaRepository
                        .findById(
                                idDisciplina
                        );

        if (disciplinaEncontrada.isEmpty()) {
            return Optional.empty();
        }

        Disciplina disciplina =
                disciplinaEncontrada.get();

        Optional<Semestre> semestre =
                semestreRepository
                        .findByIdSemestreAndIdusuario(
                                disciplina.getIdSemestre(),
                                usuario.getId()
                        );

        if (semestre.isEmpty()) {
            return Optional.empty();
        }

        Optional<Curso> curso =
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                semestre.get()
                                        .getIdCurso(),
                                usuario.getId()
                        );

        if (curso.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                disciplina
        );
    }

    private Disciplina obterDisciplinaDoUsuario(
            Integer idDisciplina,
            Usuario usuario) {

        return buscarDisciplinaDoUsuario(
                idDisciplina,
                usuario
        )
        .orElseThrow(
                () -> new IllegalArgumentException(
                        "Disciplina não encontrada."
                )
        );
    }

    private Atividade obterAtividadeDoUsuario(
            Integer idAtividade,
            Usuario usuario) {

        return buscarDoUsuario(
                idAtividade,
                usuario
        )
        .orElseThrow(
                () -> new IllegalArgumentException(
                        "Atividade não encontrada."
                )
        );
    }

    private DocumentoAtividade obterDocumentoDoUsuario(
            Integer idAtividade,
            Integer idDocumento,
            Usuario usuario) {

        obterAtividadeDoUsuario(
                idAtividade,
                usuario
        );

        return documentoAtividadeRepository
                .findByIdDocumentoAndIdAtividade(
                        idDocumento,
                        idAtividade
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Documento não encontrado."
                        )
                );
    }

    private void validarCursoPermiteAlteracao(
            Disciplina disciplina,
            Usuario usuario) {

        Semestre semestre =
                semestreRepository
                        .findByIdSemestreAndIdusuario(
                                disciplina.getIdSemestre(),
                                usuario.getId()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Semestre não encontrado."
                                )
                        );

        Curso curso =
                cursoRepository
                        .findByIdCursoAndIdUsuario(
                                semestre.getIdCurso(),
                                usuario.getId()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Curso não encontrado."
                                )
                        );

        String status =
                curso.getStatus();

        if (status == null) {
            return;
        }

        if ("TRANCADO".equalsIgnoreCase(
                status
        )) {

            throw new IllegalStateException(
                    "O curso está trancado. Os dados podem ser consultados, mas não alterados."
            );
        }

        if ("CONCLUIDO".equalsIgnoreCase(
                status
        )) {

            throw new IllegalStateException(
                    "O curso está concluído. Os dados podem ser consultados, mas não alterados."
            );
        }
    }

    private void validarDadosAtividade(
            Atividade atividade) {

        if (atividade == null) {

            throw new IllegalArgumentException(
                    "Os dados da atividade são obrigatórios."
            );
        }

        if (atividade.getNome() == null
                || atividade.getNome()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "O nome da atividade é obrigatório."
            );
        }

        if (atividade.getTipo() != null
                && !atividade.getTipo()
                        .trim()
                        .isEmpty()) {

            String tipo =
                    atividade.getTipo()
                            .trim()
                            .toUpperCase();

            if (!TIPOS_PERMITIDOS.contains(
                    tipo
            )) {

                throw new IllegalArgumentException(
                        "Tipo de atividade inválido."
                );
            }
        }

        if (atividade.getStatus() != null
                && !atividade.getStatus()
                        .trim()
                        .isEmpty()) {

            String status =
                    atividade.getStatus()
                            .trim()
                            .toUpperCase();

            if (!STATUS_PERMITIDOS.contains(
                    status
            )) {

                throw new IllegalArgumentException(
                        "Status da atividade inválido."
                );
            }
        }

        BigDecimal notaMaxima =
                atividade.getNotaMaxima();

        BigDecimal notaObtida =
                atividade.getNotaObtida();

        if (notaMaxima != null
                && notaMaxima.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

            throw new IllegalArgumentException(
                    "A nota máxima não pode ser negativa."
            );
        }

        if (notaObtida != null
                && notaObtida.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

            throw new IllegalArgumentException(
                    "A nota obtida não pode ser negativa."
            );
        }

        if (notaObtida != null
                && notaMaxima == null) {

            throw new IllegalArgumentException(
                    "Informe a nota máxima antes da nota obtida."
            );
        }

        if (notaObtida != null
                && notaMaxima != null
                && notaObtida.compareTo(
                        notaMaxima
                ) > 0) {

            throw new IllegalArgumentException(
                    "A nota obtida não pode ser maior que a nota máxima."
            );
        }

        if (atividade.getPeso() != null
                && atividade.getPeso()
                        .compareTo(
                                BigDecimal.ZERO
                        ) < 0) {

            throw new IllegalArgumentException(
                    "O peso da atividade não pode ser negativo."
            );
        }
    }

    private void prepararDados(
            Atividade atividade) {

        atividade.setNome(
                atividade.getNome()
                        .trim()
        );

        if (atividade.getTipo() == null
                || atividade.getTipo()
                        .trim()
                        .isEmpty()) {

            atividade.setTipo(
                    null
            );

        } else {

            atividade.setTipo(
                    atividade.getTipo()
                            .trim()
                            .toUpperCase()
            );
        }

        if (atividade.getStatus() == null
                || atividade.getStatus()
                        .trim()
                        .isEmpty()) {

            atividade.setStatus(
                    "PENDENTE"
            );

        } else {

            atividade.setStatus(
                    atividade.getStatus()
                            .trim()
                            .toUpperCase()
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