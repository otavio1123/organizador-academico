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

let statusCursoAnterior = null;


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

    return tipos[tipo] ||
        tipo ||
        "-";
}


function formatarStatus(status) {

    const statusFormatados = {
        ATIVO: "Ativo",
        CONCLUIDO: "Concluído",
        TRANCADO: "Trancado"
    };

    return statusFormatados[status] ||
        status ||
        "-";
}


async function obterMensagemErro(
    resposta
) {

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

    statusCursoAnterior = null;

    tituloModalCurso.textContent =
        "Novo curso";

    nomeCurso.value = "";

    instituicaoCurso.value = "";

    tipoCurso.value =
        "GRADUACAO";

    statusCurso.value =
        "ATIVO";
}


function montarDadosCurso(
    curso,
    novoStatus
) {

    return {
        nome:
            curso.nome,

        instituicao:
            curso.instituicao || null,

        tipo:
            curso.tipo,

        status:
            novoStatus
    };
}


function transicaoStatusPermitida(
    statusAnterior,
    novoStatus
) {

    if (!statusAnterior ||
            statusAnterior === novoStatus) {

        return true;
    }

    if (statusAnterior === "TRANCADO"
            && novoStatus === "CONCLUIDO") {

        return false;
    }

    if (statusAnterior === "CONCLUIDO"
            && novoStatus === "TRANCADO") {

        return false;
    }

    return true;
}


function obterMensagemTransicao(
    statusAnterior,
    novoStatus,
    nome
) {

    if (statusAnterior === novoStatus) {

        return null;
    }


    if (novoStatus === "TRANCADO") {

        return (
            `Trancar o curso "${nome}"?\n\n` +
            "O semestre ativo será desativado e " +
            "novas alterações acadêmicas ficarão bloqueadas. " +
            "Nenhum dado será apagado."
        );
    }


    if (novoStatus === "CONCLUIDO") {

        return (
            `Concluir o curso "${nome}"?\n\n` +
            "O semestre ativo será desativado e " +
            "o curso ficará disponível somente para consulta. " +
            "Nenhum dado será apagado."
        );
    }


    if (novoStatus === "ATIVO"
            && statusAnterior === "TRANCADO") {

        return (
            `Reativar o curso "${nome}"?\n\n` +
            "O curso voltará a aceitar alterações. " +
            "Nenhum semestre será reativado automaticamente."
        );
    }


    if (novoStatus === "ATIVO"
            && statusAnterior === "CONCLUIDO") {

        return (
            `Reabrir o curso "${nome}"?\n\n` +
            "O curso voltará ao status Ativo e " +
            "poderá receber novas alterações. " +
            "Nenhum semestre será reativado automaticamente."
        );
    }


    return (
        `Deseja alterar o status do curso "${nome}"?`
    );
}


async function atualizarStatusCurso(
    curso,
    novoStatus
) {

    const statusAnterior =
        curso.status || "ATIVO";

    if (!transicaoStatusPermitida(
        statusAnterior,
        novoStatus
    )) {

        if (statusAnterior === "TRANCADO") {

            alert(
                "Reative o curso antes de marcá-lo como concluído."
            );

        } else {

            alert(
                "Reabra o curso antes de marcá-lo como trancado."
            );
        }

        return;
    }


    const mensagem =
        obterMensagemTransicao(
            statusAnterior,
            novoStatus,
            curso.nome
        );


    if (mensagem &&
            !confirm(mensagem)) {

        return;
    }


    try {

        const resposta =
            await fetch(
                `${API_URL}/Cursos/${curso.idCurso}`,
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
                            montarDadosCurso(
                                curso,
                                novoStatus
                            )
                        )
                }
            );


        if (!resposta.ok) {

            const mensagemErro =
                await obterMensagemErro(
                    resposta
                );

            throw new Error(
                mensagemErro ||
                "Não foi possível alterar o status do curso."
            );
        }


        if (novoStatus === "ATIVO"
                && statusAnterior === "TRANCADO") {

            alert(
                "Curso reativado com sucesso! " +
                "Nenhum semestre foi ativado automaticamente."
            );

        } else if (
            novoStatus === "ATIVO"
            && statusAnterior === "CONCLUIDO"
        ) {

            alert(
                "Curso reaberto com sucesso! " +
                "Nenhum semestre foi ativado automaticamente."
            );

        } else if (
            novoStatus === "TRANCADO"
        ) {

            alert(
                "Curso trancado com sucesso!"
            );

        } else if (
            novoStatus === "CONCLUIDO"
        ) {

            alert(
                "Curso concluído com sucesso!"
            );
        }


        await carregarCursos();


    } catch (erro) {

        console.error(
            erro
        );

        alert(
            erro.message
        );
    }
}


function criarBotaoStatus(
    fragmento,
    curso
) {

    if (curso.status !== "TRANCADO"
            && curso.status !== "CONCLUIDO") {

        return;
    }


    const containerBotoes =
        fragmento.querySelector(
            ".mt-auto.d-grid.gap-2"
        );


    const botaoExcluir =
        fragmento.querySelector(
            ".botaoExcluirCurso"
        );


    if (!containerBotoes ||
            !botaoExcluir) {

        return;
    }


    const botao =
        document.createElement(
            "button"
        );


    botao.type =
        "button";


    botao.className =
        "btn btn-outline-success";


    if (curso.status === "TRANCADO") {

        botao.innerHTML =
            '<i class="fa-solid fa-rotate-left me-2"></i>' +
            "Reativar curso";

    } else {

        botao.innerHTML =
            '<i class="fa-solid fa-rotate-left me-2"></i>' +
            "Reabrir curso";
    }


    botao.addEventListener(
        "click",
        async () => {

            await atualizarStatusCurso(
                curso,
                "ATIVO"
            );
        }
    );


    containerBotoes.insertBefore(
        botao,
        botaoExcluir
    );
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


        const cursos =
            contexto.cursos;


        listaCursos.innerHTML =
            "";


        if (!cursos ||
                cursos.length === 0) {

            listaCursos
                .classList
                .add(
                    "d-none"
                );


            estadoVazioCursos
                .classList
                .remove(
                    "d-none"
                );


            return;
        }


        listaCursos
            .classList
            .remove(
                "d-none"
            );


        estadoVazioCursos
            .classList
            .add(
                "d-none"
            );


        cursos.forEach(
            curso => {

                const fragmento =
                    modeloCurso
                        .content
                        .cloneNode(
                            true
                        );


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
                        curso.instituicao ||
                        "-";


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


                botaoSemestres
                    .addEventListener(
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


                /*
                 * Curso trancado ou concluído fica
                 * somente para consulta.
                 *
                 * Para voltar a editar, primeiro
                 * deve ser reativado/reaberto.
                 */
                if (curso.status !== "ATIVO") {

                    botaoEditar.disabled =
                        true;

                    botaoEditar.title =
                        curso.status === "TRANCADO"
                            ? "Reative o curso para editá-lo."
                            : "Reabra o curso para editá-lo.";

                    botaoEditar.classList.add(
                        "opacity-50"
                    );
                }


                botaoEditar.addEventListener(
                    "click",
                    () => {

                        if (curso.status !== "ATIVO") {

                            return;
                        }


                        cursoEmEdicao =
                            curso.idCurso;


                        statusCursoAnterior =
                            curso.status ||
                            "ATIVO";


                        tituloModalCurso.textContent =
                            "Editar curso";


                        nomeCurso.value =
                            curso.nome ||
                            "";


                        instituicaoCurso.value =
                            curso.instituicao ||
                            "";


                        tipoCurso.value =
                            curso.tipo ||
                            "GRADUACAO";


                        statusCurso.value =
                            curso.status ||
                            "ATIVO";


                        const modal =
                            bootstrap.Modal
                                .getOrCreateInstance(
                                    modalCurso
                                );


                        modal.show();
                    }
                );


                criarBotaoStatus(
                    fragmento,
                    curso
                );


                botaoExcluir.addEventListener(
                    "click",
                    async () => {

                        const confirmar =
                            confirm(
                                `Deseja realmente excluir o curso "${curso.nome}"?\n\n` +
                                "Esta ação pode remover os dados relacionados ao curso."
                            );


                        if (!confirmar) {

                            return;
                        }


                        try {

                            const resposta =
                                await fetch(
                                    `${API_URL}/Cursos/${curso.idCurso}`,
                                    {
                                        method:
                                            "DELETE",

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
                            ) === Number(
                                curso.idCurso
                            )) {

                                CursoContexto
                                    .limparCursoSelecionado();
                            }


                            await carregarCursos();


                        } catch (erro) {

                            console.error(
                                erro
                            );


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

        console.error(
            erro
        );


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
            nomeCurso
                .value
                .trim();


        const instituicao =
            instituicaoCurso
                .value
                .trim();


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


        /*
         * Impede transição direta:
         *
         * TRANCADO -> CONCLUIDO
         * CONCLUIDO -> TRANCADO
         */
        if (cursoEmEdicao
                && !transicaoStatusPermitida(
                    statusCursoAnterior,
                    status
                )) {

            if (statusCursoAnterior === "TRANCADO") {

                alert(
                    "Reative o curso antes de marcá-lo como concluído."
                );

            } else {

                alert(
                    "Reabra o curso antes de marcá-lo como trancado."
                );
            }

            return;
        }


        /*
         * Confirma mudanças importantes de status.
         */
        if (cursoEmEdicao
                && statusCursoAnterior !== status) {

            const mensagem =
                obterMensagemTransicao(
                    statusCursoAnterior,
                    status,
                    nome
                );


            if (mensagem
                    && !confirm(
                        mensagem
                    )) {

                return;
            }
        }


        const dadosCurso = {

            nome:
                nome,

            instituicao:
                instituicao ||
                null,

            tipo:
                tipo,

            status:
                status
        };


        salvarCurso.disabled =
            true;


        try {

            let resposta;


            if (cursoEmEdicao) {

                resposta =
                    await fetch(
                        `${API_URL}/Cursos/${cursoEmEdicao}`,
                        {
                            method:
                                "PUT",

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
                            method:
                                "POST",

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
                Boolean(
                    cursoEmEdicao
                );


            const statusAnterior =
                statusCursoAnterior;


            const modal =
                bootstrap.Modal
                    .getInstance(
                        modalCurso
                    );


            if (modal) {

                modal.hide();
            }


            if (!estavaEditando) {

                alert(
                    "Curso cadastrado com sucesso!"
                );

            } else if (
                statusAnterior === "ATIVO"
                && status === "TRANCADO"
            ) {

                alert(
                    "Curso trancado com sucesso! " +
                    "O semestre ativo foi desativado."
                );

            } else if (
                statusAnterior === "ATIVO"
                && status === "CONCLUIDO"
            ) {

                alert(
                    "Curso concluído com sucesso! " +
                    "O semestre ativo foi desativado."
                );

            } else {

                alert(
                    "Curso atualizado com sucesso!"
                );
            }


            limparModal();


            await carregarCursos();


        } catch (erro) {

            console.error(
                erro
            );


            alert(
                erro.message
            );


        } finally {

            salvarCurso.disabled =
                false;
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