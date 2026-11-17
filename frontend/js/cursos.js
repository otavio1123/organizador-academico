const token =
    localStorage.getItem("token");

const usuarioLogado =
    JSON.parse(
        localStorage.getItem("usuarioLogado")
    );

if (!token || !usuarioLogado) {
    localStorage.removeItem("token");
    localStorage.removeItem("usuarioLogado");
    window.location.href = "index.html";
}

const listaCursos =
    document.getElementById("listaCursos");

const estadoVazioCursos =
    document.getElementById("estadoVazioCursos");

const modeloCurso =
    document.getElementById("modeloCurso");

const botaoNovoCurso =
    document.getElementById("botaoNovoCurso");

const salvarCurso =
    document.getElementById("salvarCurso");

const modalCurso =
    document.getElementById("modalCurso");

const tituloModalCurso =
    document.getElementById("tituloModalCurso");

const nomeCurso =
    document.getElementById("nomeCurso");

const instituicaoCurso =
    document.getElementById("instituicaoCurso");

const tipoCurso =
    document.getElementById("tipoCurso");

const statusCurso =
    document.getElementById("statusCurso");

const nomeUsuario =
    document.getElementById("nomeUsuarioLogado");

const emailUsuario =
    document.getElementById("emailUsuarioLogado");

const imagemUsuarioTopo =
    document.getElementById("imagemUsuarioTopo");

const iconeUsuarioTopo =
    document.getElementById("iconeUsuarioTopo");

let cursoEmEdicao = null;

if (nomeUsuario) {
    nomeUsuario.textContent =
        usuarioLogado.nome || "Usuário";
}

if (emailUsuario) {
    emailUsuario.textContent =
        usuarioLogado.email || "";
}

function formatarTipo(tipo) {

    const tipos = {
        GRADUACAO: "Graduação",
        POS_GRADUACAO: "Pós-graduação",
        TECNICO: "Técnico",
        EXTENSAO: "Extensão",
        OUTRO: "Outro"
    };

    return tipos[tipo] || tipo || "-";
}

function formatarStatus(status) {

    const statusFormatados = {
        ATIVO: "Ativo",
        CONCLUIDO: "Concluído",
        TRANCADO: "Trancado"
    };

    return statusFormatados[status] || status || "-";
}

async function obterMensagemErro(resposta) {

    try {

        const dados =
            await resposta.json();

        return dados.mensagem ||
            JSON.stringify(dados);

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
                    headers: {
                        "Authorization":
                            `Bearer ${token}`
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

            localStorage.setItem(
                "usuarioLogado",
                JSON.stringify(usuarioLogado)
            );

            if (nomeUsuario) {
                nomeUsuario.textContent =
                    dados.usuario.nome || "Usuário";
            }

            if (emailUsuario) {
                emailUsuario.textContent =
                    dados.usuario.email || "";
            }
        }

        if (dados.fotoUrl) {

            if (imagemUsuarioTopo) {

                imagemUsuarioTopo.src =
                    dados.fotoUrl;

                imagemUsuarioTopo
                    .classList
                    .remove("d-none");
            }

            if (iconeUsuarioTopo) {
                iconeUsuarioTopo
                    .classList
                    .add("d-none");
            }

        } else {

            if (imagemUsuarioTopo) {
                imagemUsuarioTopo.src = "";
                imagemUsuarioTopo
                    .classList
                    .add("d-none");
            }

            if (iconeUsuarioTopo) {
                iconeUsuarioTopo
                    .classList
                    .remove("d-none");
            }
        }

    } catch (erro) {

        console.error(
            "Erro ao carregar usuário:",
            erro
        );
    }
}

function limparModal() {

    cursoEmEdicao = null;

    tituloModalCurso.textContent =
        "Novo curso";

    nomeCurso.value = "";
    instituicaoCurso.value = "";
    tipoCurso.value = "GRADUACAO";
    statusCurso.value = "ATIVO";
}

botaoNovoCurso.addEventListener(
    "click",
    () => {

        limparModal();
    }
);

async function carregarCursos() {

    try {

        const contexto =
            await CursoContexto
                .inicializarSeletorGlobal();

        const cursos = contexto.cursos;

        listaCursos.innerHTML = "";

        if (!cursos || cursos.length === 0) {

            listaCursos.classList.add(
                "d-none"
            );

            estadoVazioCursos
                .classList
                .remove("d-none");

            return;
        }

        listaCursos.classList.remove(
            "d-none"
        );

        estadoVazioCursos
            .classList
            .add("d-none");

        cursos.forEach(
            curso => {

                const fragmento =
                    modeloCurso
                        .content
                        .cloneNode(true);

                fragmento
                    .querySelector(
                        ".nome-curso"
                    )
                    .textContent =
                        curso.nome;

                fragmento
                    .querySelector(
                        ".instituicao-curso"
                    )
                    .textContent =
                        curso.instituicao || "-";

                fragmento
                    .querySelector(
                        ".tipo-curso"
                    )
                    .textContent =
                        formatarTipo(
                            curso.tipo
                        );

                fragmento
                    .querySelector(
                        ".status-curso"
                    )
                    .textContent =
                        formatarStatus(
                            curso.status
                        );

                const botaoSemestres =
                    fragmento.querySelector(
                        ".botaoSemestres"
                    );

                botaoSemestres.href =
                    `semestres.html?idCurso=${curso.idCurso}`;

                botaoSemestres.addEventListener(
                    "click",
                    () => {
                        CursoContexto
                            .salvarCursoSelecionado(
                                curso.idCurso
                            );
                    }
                );

                const botaoEditar =
                    fragmento.querySelector(
                        ".botaoEditarCurso"
                    );

                const botaoExcluir =
                    fragmento.querySelector(
                        ".botaoExcluirCurso"
                    );

                botaoEditar.addEventListener(
                    "click",
                    () => {

                        cursoEmEdicao =
                            curso.idCurso;

                        tituloModalCurso.textContent =
                            "Editar curso";

                        nomeCurso.value =
                            curso.nome || "";

                        instituicaoCurso.value =
                            curso.instituicao || "";

                        tipoCurso.value =
                            curso.tipo || "GRADUACAO";

                        statusCurso.value =
                            curso.status || "ATIVO";

                        const modal =
                            bootstrap.Modal
                                .getOrCreateInstance(
                                    modalCurso
                                );

                        modal.show();
                    }
                );

                botaoExcluir.addEventListener(
                    "click",
                    async () => {

                        const confirmar =
                            confirm(
                                `Deseja realmente excluir o curso "${curso.nome}"?`
                            );

                        if (!confirmar) {
                            return;
                        }

                        try {

                            const resposta =
                                await fetch(
                                    `${API_URL}/Cursos/${curso.idCurso}`,
                                    {
                                        method: "DELETE",
                                        headers: {
                                            "Authorization":
                                                `Bearer ${token}`
                                        }
                                    }
                                );

                            if (!resposta.ok) {

                                const mensagem =
                                    await obterMensagemErro(
                                        resposta
                                    );

                                throw new Error(
                                    mensagem ||
                                    "Não foi possível excluir o curso."
                                );
                            }

                            alert(
                                "Curso excluído com sucesso!"
                            );

                            if (Number(
                                CursoContexto
                                    .obterCursoSelecionadoId()
                            ) === Number(curso.idCurso)) {

                                CursoContexto
                                    .limparCursoSelecionado();
                            }

                            await carregarCursos();

                        } catch (erro) {

                            console.error(erro);

                            alert(
                                erro.message
                            );
                        }
                    }
                );

                listaCursos.appendChild(
                    fragmento
                );
            }
        );

    } catch (erro) {

        console.error(erro);

        alert(
            erro.message ||
            "Não foi possível carregar os cursos."
        );
    }
}

salvarCurso.addEventListener(
    "click",
    async () => {

        const nome =
            nomeCurso.value.trim();

        const instituicao =
            instituicaoCurso.value.trim();

        const tipo =
            tipoCurso.value;

        const status =
            statusCurso.value;

        if (!nome) {

            alert(
                "Informe o nome do curso."
            );

            return;
        }

        const dadosCurso = {
            nome: nome,
            instituicao:
                instituicao || null,
            tipo: tipo,
            status: status
        };

        salvarCurso.disabled = true;

        try {

            let resposta;

            if (cursoEmEdicao) {

                resposta =
                    await fetch(
                        `${API_URL}/Cursos/${cursoEmEdicao}`,
                        {
                            method: "PUT",
                            headers: {
                                "Content-Type":
                                    "application/json",
                                "Authorization":
                                    `Bearer ${token}`
                            },
                            body:
                                JSON.stringify(
                                    dadosCurso
                                )
                        }
                    );

            } else {

                resposta =
                    await fetch(
                        `${API_URL}/Cursos`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type":
                                    "application/json",
                                "Authorization":
                                    `Bearer ${token}`
                            },
                            body:
                                JSON.stringify(
                                    dadosCurso
                                )
                        }
                    );
            }

            if (!resposta.ok) {

                const mensagem =
                    await obterMensagemErro(
                        resposta
                    );

                throw new Error(
                    mensagem ||
                    "Não foi possível salvar o curso."
                );
            }

            const estavaEditando =
                Boolean(cursoEmEdicao);

            const modal =
                bootstrap.Modal
                    .getInstance(
                        modalCurso
                    );

            if (modal) {
                modal.hide();
            }

            alert(
                estavaEditando
                    ? "Curso atualizado com sucesso!"
                    : "Curso cadastrado com sucesso!"
            );

            limparModal();

            await carregarCursos();

        } catch (erro) {

            console.error(erro);

            alert(
                erro.message
            );

        } finally {

            salvarCurso.disabled = false;
        }
    }
);

modalCurso.addEventListener(
    "hidden.bs.modal",
    () => {

        limparModal();
    }
);

carregarUsuarioTopo();
carregarCursos();
