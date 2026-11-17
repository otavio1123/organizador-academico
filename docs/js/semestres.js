const filtroStatusSemestre =
    document.getElementById("filtroStatusSemestre");

const botaoCriarSemestres =
    document.getElementById("botao-criarSemestres");

const modalCriarSemestre =
    document.getElementById("modalCriarSemestre");

const salvarSemestre =
    document.getElementById("salvarSemestre");

const tituloModalSemestre =
    document.getElementById("tituloModalSemestre");

const token =
    localStorage.getItem("token");

const usuarioLogado =
    JSON.parse(
        localStorage.getItem("usuarioLogado")
    );

if (!token || !usuarioLogado) {

    localStorage.removeItem("token");
    localStorage.removeItem("usuarioLogado");

    window.location.href =
        "index.html";
}

let idCurso = null;

const nomeCursoAtual =
    document.getElementById(
        "nomeCursoAtual"
    );

const nomeUsuario =
    document.getElementById(
        "nomeUsuarioLogado"
    );

const emailUsuario =
    document.getElementById(
        "emailUsuarioLogado"
    );

const imagemUsuarioTopo =
    document.getElementById(
        "imagemUsuarioTopo"
    );

const iconeUsuarioTopo =
    document.getElementById(
        "iconeUsuarioTopo"
    );

let idSemestreSelecionado = null;

if (nomeUsuario) {

    nomeUsuario.textContent =
        usuarioLogado.nome ||
        "Usuário";
}

if (emailUsuario) {

    emailUsuario.textContent =
        usuarioLogado.email ||
        "";
}

async function obterMensagemErro(
    resposta
) {

    try {

        const dados =
            await resposta.json();

        if (dados.mensagem) {
            return dados.mensagem;
        }

        return JSON.stringify(
            dados
        );

    } catch {

        try {

            return await resposta.text();

        } catch {

            return "Ocorreu um erro.";
        }
    }
}

async function carregarUsuarioTopo() {

    try {

        const resposta =
            await fetch(
                `${API_URL}/usuarios/perfil`,
                {
                    method: "GET",
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );

        if (!resposta.ok) {
            return;
        }

        const dados =
            await resposta.json();

        if (dados.usuario) {

            usuarioLogado.nome =
                dados.usuario.nome;

            usuarioLogado.email =
                dados.usuario.email;

            usuarioLogado.fotoPerfil =
                dados.usuario.fotoPerfil;

            localStorage.setItem(
                "usuarioLogado",
                JSON.stringify(
                    usuarioLogado
                )
            );

            if (nomeUsuario) {

                nomeUsuario.textContent =
                    dados.usuario.nome ||
                    "Usuário";
            }

            if (emailUsuario) {

                emailUsuario.textContent =
                    dados.usuario.email ||
                    "";
            }
        }

        if (dados.fotoUrl) {

            if (imagemUsuarioTopo) {

                imagemUsuarioTopo.src =
                    dados.fotoUrl;

                imagemUsuarioTopo
                    .classList
                    .remove(
                        "d-none"
                    );
            }

            if (iconeUsuarioTopo) {

                iconeUsuarioTopo
                    .classList
                    .add(
                        "d-none"
                    );
            }

        } else {

            if (imagemUsuarioTopo) {

                imagemUsuarioTopo.src =
                    "";

                imagemUsuarioTopo
                    .classList
                    .add(
                        "d-none"
                    );
            }

            if (iconeUsuarioTopo) {

                iconeUsuarioTopo
                    .classList
                    .remove(
                        "d-none"
                    );
            }
        }

    } catch (erro) {

        console.error(
            "Erro ao carregar usuário:",
            erro
        );
    }
}

function obterPeriodo(
    dataInicio,
    dataFim
) {

    if (!dataInicio
        || !dataFim) {

        return "-";
    }

    const anoInicio =
        Number(
            dataInicio.substring(
                0,
                4
            )
        );

    const anoFim =
        Number(
            dataFim.substring(
                0,
                4
            )
        );

    if (anoInicio === anoFim) {

        return `${anoInicio}`;
    }

    return `${anoInicio}-${anoFim}`;
}

function formatarData(
    data
) {

    if (!data) {
        return "-";
    }

    const partes =
        data.split("-");

    if (partes.length !== 3) {
        return data;
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function limparFormularioSemestre() {

    idSemestreSelecionado =
        null;

    tituloModalSemestre.textContent =
        "Criar novo semestre";

    document
        .getElementById(
            "nomeSemestre"
        )
        .value = "";

    const campoAno =
        document.getElementById(
            "anoSemestre"
        );

    if (campoAno) {
        campoAno.value = "";
    }

    document
        .getElementById(
            "inicioSemestre"
        )
        .value = "";

    document
        .getElementById(
            "fimSemestre"
        )
        .value = "";

    document
        .getElementById(
            "ativoSemestre"
        )
        .value = "S";
}

filtroStatusSemestre
    .addEventListener(
        "change",
        () => {

            carregarSemestres();
        }
    );

botaoCriarSemestres
    .addEventListener(
        "click",
        async () => {

            limparFormularioSemestre();
        }
    );

salvarSemestre
    .addEventListener(
        "click",
        async () => {

            if (!idCurso) {
                return;
            }

            const nome =
                document
                    .getElementById(
                        "nomeSemestre"
                    )
                    .value
                    .trim();

            const dataInicio =
                document
                    .getElementById(
                        "inicioSemestre"
                    )
                    .value;

            const dataFim =
                document
                    .getElementById(
                        "fimSemestre"
                    )
                    .value;

            const ativo =
                document
                    .getElementById(
                        "ativoSemestre"
                    )
                    .value;

            if (
                !nome ||
                !dataInicio ||
                !dataFim
            ) {

                alert(
                    "Preencha todos os campos."
                );

                return;
            }

            if (dataFim <= dataInicio) {

                alert(
                    "A data de fim deve ser maior que a data de início."
                );

                return;
            }

            const anoInicio =
                Number(
                    dataInicio.substring(
                        0,
                        4
                    )
                );

            const semestre = {

                idCurso:
                    idCurso,

                nome:
                    nome,

                ano:
                    anoInicio,

                dataInicio:
                    dataInicio,

                dataFim:
                    dataFim,

                ativo:
                    ativo
            };

            salvarSemestre.disabled =
                true;

            try {

                let resposta;

                if (
                    idSemestreSelecionado ===
                    null
                ) {

                    resposta =
                        await fetch(
                            `${API_URL}/Semestres`,
                            {
                                method:
                                    "POST",

                                headers: {

                                    "Content-Type":
                                        "application/json",

                                    "Authorization":
                                        "Bearer " +
                                        token
                                },

                                body:
                                    JSON.stringify(
                                        semestre
                                    )
                            }
                        );

                } else {

                    resposta =
                        await fetch(
                            `${API_URL}/Semestres/${idSemestreSelecionado}`,
                            {
                                method:
                                    "PUT",

                                headers: {

                                    "Content-Type":
                                        "application/json",

                                    "Authorization":
                                        "Bearer " +
                                        token
                                },

                                body:
                                    JSON.stringify(
                                        semestre
                                    )
                            }
                        );
                }

                if (!resposta.ok) {

                    const mensagem =
                        await obterMensagemErro(
                            resposta
                        );

                    alert(
                        mensagem ||
                        "Não foi possível salvar o semestre."
                    );

                    return;
                }

                await resposta.json();

                const estavaEditando =
                    idSemestreSelecionado !==
                    null;

                if (estavaEditando) {

                    alert(
                        "Semestre alterado com sucesso!"
                    );

                } else {

                    alert(
                        "Semestre criado com sucesso!"
                    );
                }

                const modal =
                    bootstrap.Modal
                        .getInstance(
                            modalCriarSemestre
                        );

                if (modal) {
                    modal.hide();
                }

                limparFormularioSemestre();

                await carregarSemestres();

            } catch (erro) {

                console.error(
                    erro
                );

                alert(
                    "Não foi possível salvar o semestre."
                );

            } finally {

                salvarSemestre.disabled =
                    false;
            }
        }
    );

async function carregarSemestres() {

    if (!idCurso) {
        return;
    }

    try {

        const resposta =
            await fetch(
                `${API_URL}/Cursos/${idCurso}/Semestres`,
                {
                    method:
                        "GET",

                    headers: {

                        "Authorization":
                            "Bearer " +
                            token
                    }
                }
            );

        if (!resposta.ok) {

            const mensagem =
                await obterMensagemErro(
                    resposta
                );

            alert(
                mensagem ||
                "Não foi possível carregar os semestres."
            );

            return;
        }

        const semestres =
            await resposta.json();

        const listaSemestres =
            document.getElementById(
                "listaSemestres"
            );

        listaSemestres.innerHTML =
            "";

        const statusEscolhido =
            filtroStatusSemestre.value;

        const semestresFiltrados =
            semestres.filter(
                semestre => {

                    return semestre.ativo ===
                        statusEscolhido;
                }
            );

        semestresFiltrados.forEach(
            semestre => {

                const modelo =
                    document.getElementById(
                        "modeloSemestre"
                    );

                const card =
                    modelo
                        .content
                        .cloneNode(
                            true
                        );

                card
                    .querySelector(
                        ".nome-semestre"
                    )
                    .textContent =
                        semestre.nome;

                card
                    .querySelector(
                        ".ano-semestre"
                    )
                    .textContent =
                        obterPeriodo(
                            semestre.dataInicio,
                            semestre.dataFim
                        );

                card
                    .querySelector(
                        ".inicio-semestre"
                    )
                    .textContent =
                        formatarData(
                            semestre.dataInicio
                        );

                card
                    .querySelector(
                        ".fim-semestre"
                    )
                    .textContent =
                        formatarData(
                            semestre.dataFim
                        );

                const botaoDisciplinas =
                    card.querySelector(
                        ".botao-disciplinas"
                    );

                botaoDisciplinas.href =
                    `disciplinas.html?idSemestre=${semestre.idSemestre}&idCurso=${idCurso}`;

                const botaoExcluir =
                    card.querySelector(
                        ".botao-excluir-semestre"
                    );

                const botaoOpcoes =
                    card.querySelector(
                        ".botaoOpcoesSemestre"
                    );

                botaoOpcoes
                    .addEventListener(
                        "click",
                        () => {

                            idSemestreSelecionado =
                                semestre.idSemestre;

                            document
                                .getElementById(
                                    "nomeSemestre"
                                )
                                .value =
                                    semestre.nome;

                            const campoAno =
                                document.getElementById(
                                    "anoSemestre"
                                );

                            if (campoAno) {

                                campoAno.value =
                                    semestre.ano ||
                                    "";
                            }

                            document
                                .getElementById(
                                    "ativoSemestre"
                                )
                                .value =
                                    semestre.ativo;

                            document
                                .getElementById(
                                    "inicioSemestre"
                                )
                                .value =
                                    semestre.dataInicio;

                            document
                                .getElementById(
                                    "fimSemestre"
                                )
                                .value =
                                    semestre.dataFim;

                            tituloModalSemestre
                                .textContent =
                                    "Editar semestre";

                            const modal =
                                bootstrap.Modal
                                    .getOrCreateInstance(
                                        modalCriarSemestre
                                    );

                            modal.show();
                        }
                    );

                botaoExcluir
                    .addEventListener(
                        "click",
                        async () => {

                            const confirmar =
                                confirm(
                                    `Deseja realmente excluir o semestre "${semestre.nome}"?`
                                );

                            if (!confirmar) {
                                return;
                            }

                            try {

                                const resposta =
                                    await fetch(
                                        `${API_URL}/Semestres/${semestre.idSemestre}`,
                                        {
                                            method:
                                                "DELETE",

                                            headers: {

                                                "Authorization":
                                                    "Bearer " +
                                                    token
                                            }
                                        }
                                    );

                                if (!resposta.ok) {

                                    const mensagem =
                                        await obterMensagemErro(
                                            resposta
                                        );

                                    alert(
                                        mensagem ||
                                        "Não foi possível excluir o semestre."
                                    );

                                    return;
                                }

                                alert(
                                    "Semestre excluído com sucesso!"
                                );

                                await carregarSemestres();

                            } catch (erro) {

                                console.error(
                                    "Erro:",
                                    erro
                                );

                                alert(
                                    "Não foi possível excluir o semestre."
                                );
                            }
                        }
                    );

                listaSemestres
                    .appendChild(
                        card
                    );
            }
        );

    } catch (erro) {

        console.error(
            "Erro:",
            erro
        );

        alert(
            "Não foi possível carregar os semestres."
        );
    }
}

async function iniciarPagina() {

    try {

        const contexto =
            await CursoContexto
                .inicializarSeletorGlobal({
                    aoAlterar: curso => {
                        window.location.href =
                            `semestres.html?idCurso=${curso.idCurso}`;
                    }
                });

        if (!contexto.cursoSelecionado) {
            window.location.href = "cursos.html";
            return;
        }

        idCurso = contexto.idCurso;

        if (nomeCursoAtual) {
            nomeCursoAtual.textContent =
                contexto.cursoSelecionado.nome;
        }

        const parametros = new URLSearchParams(
            window.location.search
        );

        if (Number(parametros.get("idCurso")) !== idCurso) {
            window.history.replaceState(
                {},
                "",
                `semestres.html?idCurso=${idCurso}`
            );
        }

    } catch (erro) {

        console.error(
            "Erro ao identificar curso:",
            erro
        );

        alert(erro.message);
        return;
    }

    await carregarUsuarioTopo();

    await carregarSemestres();
}

iniciarPagina();
