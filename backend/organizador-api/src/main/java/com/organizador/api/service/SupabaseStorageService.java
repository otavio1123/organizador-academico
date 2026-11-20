package com.organizador.api.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Service
public class SupabaseStorageService {

    private static final String BUCKET_FOTOS =
            "fotos-perfil";

    private static final String BUCKET_ATIVIDADES =
            "atividades-documentos";

    private static final long TAMANHO_MAXIMO_DOCUMENTO =
            10L * 1024 * 1024;

    private static final Set<String> EXTENSOES_PERMITIDAS =
            Set.of(
                    ".pdf",
                    ".doc",
                    ".docx",
                    ".xls",
                    ".xlsx",
                    ".ppt",
                    ".pptx",
                    ".png",
                    ".jpg",
                    ".jpeg"
            );

    private static final Set<String> MIME_TYPES_PERMITIDOS =
            Set.of(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "image/png",
                    "image/jpeg"
            );

    private final String supabaseUrl;
    private final String supabaseSecretKey;
    private final JsonMapper jsonMapper;

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    public SupabaseStorageService(
            @Value("${supabase.url}")
            String supabaseUrl,

            @Value("${supabase.secret-key}")
            String supabaseSecretKey,

            JsonMapper jsonMapper) {

        this.supabaseUrl = supabaseUrl;
        this.supabaseSecretKey = supabaseSecretKey;
        this.jsonMapper = jsonMapper;
    }

    public String enviarFoto(
            MultipartFile arquivo,
            Integer idUsuario) {

        if (arquivo == null
                || arquivo.isEmpty()) {

            throw new IllegalArgumentException(
                    "O arquivo da foto é obrigatório."
            );
        }

        String extensao =
                extrairExtensao(
                        arquivo.getOriginalFilename()
                );

        String nomeArquivo =
                idUsuario
                + "-"
                + System.currentTimeMillis()
                + extensao;

        String caminho =
                idUsuario
                + "/"
                + nomeArquivo;

        enviarArquivo(
                arquivo,
                BUCKET_FOTOS,
                caminho,
                "foto"
        );

        return caminho;
    }

    public String gerarUrlTemporaria(
            String caminhoFoto) {

        return gerarUrlTemporariaDoBucket(
                BUCKET_FOTOS,
                caminhoFoto,
                3600,
                "foto"
        );
    }

    public String enviarDocumentoAtividade(
            MultipartFile arquivo,
            Integer idUsuario,
            Integer idAtividade) {

        validarDocumentoAtividade(
                arquivo
        );

        String extensao =
                extrairExtensao(
                        arquivo.getOriginalFilename()
                )
                .toLowerCase();

        String nomeArquivo =
                UUID.randomUUID()
                + extensao;

        String caminho =
                "usuario-"
                + idUsuario
                + "/atividade-"
                + idAtividade
                + "/"
                + nomeArquivo;

        enviarArquivo(
                arquivo,
                BUCKET_ATIVIDADES,
                caminho,
                "documento"
        );

        return caminho;
    }

    public String gerarUrlTemporariaDocumento(
            String caminhoDocumento) {

        return gerarUrlTemporariaDoBucket(
                BUCKET_ATIVIDADES,
                caminhoDocumento,
                300,
                "documento"
        );
    }

    public void excluirDocumentoAtividade(
            String caminhoDocumento) {

        if (caminhoDocumento == null
                || caminhoDocumento.isBlank()) {

            return;
        }

        try {

            String url =
                    supabaseUrl
                    + "/storage/v1/object/"
                    + BUCKET_ATIVIDADES
                    + "/"
                    + caminhoDocumento;

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            url
                                    )
                            )
                            .header(
                                    "Authorization",
                                    "Bearer "
                                    + supabaseSecretKey
                            )
                            .header(
                                    "apikey",
                                    supabaseSecretKey
                            )
                            .DELETE()
                            .build();

            HttpResponse<String> resposta =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (resposta.statusCode() < 200
                    || resposta.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Erro ao excluir documento do Storage: "
                        + resposta.body()
                );
            }

        } catch (InterruptedException erro) {

            Thread.currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "Não foi possível excluir o documento.",
                    erro
            );

        } catch (IOException erro) {

            throw new IllegalStateException(
                    "Não foi possível excluir o documento.",
                    erro
            );
        }
    }

    private void validarDocumentoAtividade(
            MultipartFile arquivo) {

        if (arquivo == null
                || arquivo.isEmpty()) {

            throw new IllegalArgumentException(
                    "Selecione um arquivo."
            );
        }

        if (arquivo.getSize()
                > TAMANHO_MAXIMO_DOCUMENTO) {

            throw new IllegalArgumentException(
                    "O arquivo deve possuir no máximo 10 MB."
            );
        }

        String nomeOriginal =
                arquivo.getOriginalFilename();

        String extensao =
                extrairExtensao(
                        nomeOriginal
                )
                .toLowerCase();

        if (!EXTENSOES_PERMITIDAS
                .contains(
                        extensao
                )) {

            throw new IllegalArgumentException(
                    "Formato de arquivo não permitido."
            );
        }

        String tipoMime =
                arquivo.getContentType();

        if (tipoMime == null
                || tipoMime.isBlank()) {

            throw new IllegalArgumentException(
                    "Não foi possível identificar o tipo do arquivo."
            );
        }

        tipoMime =
                tipoMime
                        .split(";")[0]
                        .trim()
                        .toLowerCase();

        if (!MIME_TYPES_PERMITIDOS
                .contains(
                        tipoMime
                )) {

            throw new IllegalArgumentException(
                    "Tipo de arquivo não permitido."
            );
        }
    }

    private void enviarArquivo(
            MultipartFile arquivo,
            String bucket,
            String caminho,
            String descricao) {

        try {

            String tipoMime =
                    arquivo.getContentType();

            if (tipoMime == null
                    || tipoMime.isBlank()) {

                tipoMime =
                        "application/octet-stream";
            }

            String url =
                    supabaseUrl
                    + "/storage/v1/object/"
                    + bucket
                    + "/"
                    + caminho;

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            url
                                    )
                            )
                            .header(
                                    "Authorization",
                                    "Bearer "
                                    + supabaseSecretKey
                            )
                            .header(
                                    "apikey",
                                    supabaseSecretKey
                            )
                            .header(
                                    "Content-Type",
                                    tipoMime
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofByteArray(
                                                    arquivo.getBytes()
                                            )
                            )
                            .build();

            HttpResponse<String> resposta =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (resposta.statusCode() < 200
                    || resposta.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Erro ao enviar "
                        + descricao
                        + ": "
                        + resposta.body()
                );
            }

        } catch (InterruptedException erro) {

            Thread.currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "Não foi possível enviar "
                    + descricao
                    + ".",
                    erro
            );

        } catch (IOException erro) {

            throw new IllegalStateException(
                    "Não foi possível enviar "
                    + descricao
                    + ".",
                    erro
            );
        }
    }

    private String gerarUrlTemporariaDoBucket(
            String bucket,
            String caminho,
            int validadeSegundos,
            String descricao) {

        if (caminho == null
                || caminho.isBlank()) {

            return null;
        }

        try {

            String url =
                    supabaseUrl
                    + "/storage/v1/object/sign/"
                    + bucket
                    + "/"
                    + caminho;

            String corpo =
                    """
                    {
                        "expiresIn": %d
                    }
                    """
                    .formatted(
                            validadeSegundos
                    );

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            url
                                    )
                            )
                            .header(
                                    "Authorization",
                                    "Bearer "
                                    + supabaseSecretKey
                            )
                            .header(
                                    "apikey",
                                    supabaseSecretKey
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    corpo
                                            )
                            )
                            .build();

            HttpResponse<String> resposta =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (resposta.statusCode() < 200
                    || resposta.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Erro ao gerar URL do "
                        + descricao
                        + ": "
                        + resposta.body()
                );
            }

JsonNode json =
        jsonMapper.readTree(
                resposta.body()
        );
            String signedUrl = null;

            if (json.hasNonNull(
                    "signedURL"
            )) {

                signedUrl =
                        json.get(
                                "signedURL"
                        )
                        .asText();

            } else if (json.hasNonNull(
                    "signedUrl"
            )) {

                signedUrl =
                        json.get(
                                "signedUrl"
                        )
                        .asText();
            }

            if (signedUrl == null
                    || signedUrl.isBlank()) {

                throw new IllegalStateException(
                        "URL temporária não encontrada na resposta do Storage."
                );
            }

            if (signedUrl.startsWith(
                    "http://"
            )
                    || signedUrl.startsWith(
                            "https://"
                    )) {

                return signedUrl;
            }

            return supabaseUrl
                    + "/storage/v1"
                    + signedUrl;

        } catch (InterruptedException erro) {

            Thread.currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "Não foi possível carregar o "
                    + descricao
                    + ".",
                    erro
            );

        } catch (IOException erro) {

            throw new IllegalStateException(
                    "Não foi possível carregar o "
                    + descricao
                    + ".",
                    erro
            );
        }
    }

    private String extrairExtensao(
            String nomeArquivo) {

        if (nomeArquivo == null
                || nomeArquivo.isBlank()
                || !nomeArquivo.contains(".")) {

            return "";
        }

        return nomeArquivo.substring(
                nomeArquivo.lastIndexOf(".")
        );
    }
}