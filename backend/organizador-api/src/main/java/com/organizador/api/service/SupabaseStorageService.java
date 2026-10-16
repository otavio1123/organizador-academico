package com.organizador.api.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    private final String supabaseUrl;
    private final String supabaseSecretKey;

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    public SupabaseStorageService(
            @Value("${supabase.url}")
            String supabaseUrl,

            @Value("${supabase.secret-key}")
            String supabaseSecretKey) {

        this.supabaseUrl = supabaseUrl;
        this.supabaseSecretKey = supabaseSecretKey;
    }

    public String enviarFoto(
            MultipartFile arquivo,
            Integer idUsuario) {

        try {

            String nomeOriginal =
                    arquivo.getOriginalFilename();

            String extensao = "";

            if (nomeOriginal != null
                    && nomeOriginal.contains(".")) {

                extensao =
                        nomeOriginal.substring(
                                nomeOriginal.lastIndexOf(".")
                        );
            }

            String nomeArquivo =
                    idUsuario
                    + "-"
                    + System.currentTimeMillis()
                    + extensao;

            String caminho =
                    idUsuario
                    + "/"
                    + nomeArquivo;

            String url =
                    supabaseUrl
                    + "/storage/v1/object/fotos-perfil/"
                    + caminho;

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
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
                                    arquivo.getContentType()
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
                        "Erro ao enviar foto: "
                        + resposta.body()
                );
            }

            return caminho;

        } catch (IOException
                | InterruptedException erro) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Não foi possível enviar a foto.",
                    erro
            );
        }
    }

    public String gerarUrlTemporaria(
            String caminhoFoto) {

        if (caminhoFoto == null
                || caminhoFoto.isBlank()) {

            return null;
        }

        try {

            String url =
                    supabaseUrl
                    + "/storage/v1/object/sign/fotos-perfil/"
                    + caminhoFoto;

            String corpo =
                    """
                    {
                        "expiresIn": 3600
                    }
                    """;

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
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
                                            .ofString(corpo)
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
                        "Erro ao gerar URL da foto: "
                        + resposta.body()
                );
            }

            String respostaBody =
                    resposta.body();

            String signedUrl =
                    respostaBody
                            .replace(
                                    "{\"signedURL\":\"",
                                    ""
                            )
                            .replace(
                                    "\"}",
                                    ""
                            );

            return supabaseUrl
                    + "/storage/v1"
                    + signedUrl;

        } catch (IOException
                | InterruptedException erro) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Não foi possível carregar a foto.",
                    erro
            );
        }
    }
}