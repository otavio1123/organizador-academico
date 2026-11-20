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

const parametros =
    new URLSearchParams(
        window.location.search
    );

const idCurso =
    Number(
        parametros.get("idCurso")
    );

const idSemestre =
    Number(
        parametros.get("idSemestre")
    );

const idDisciplina =
    Number(
        parametros.get("idDisciplina")
    );

const nomeUsuarioLogado =
    document.getElementById(
        "nomeUsuarioLogado"
    );

const emailUsuarioLogado =
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

const nomeDisciplinaAtual =
    document.getElementById(
        "nomeDisciplinaAtual"
    );

const nomeSemestreAtual =
    document.getElementById(
        "nomeSemestreAtual"
    );

const statusCursoAtual =
    document.getElementById(
        "statusCursoAtual"
    );

const avisoSomenteLeitura =
    document.getElementById(
        "avisoSomenteLeitura"
    );

const tituloSomenteLeitura =
    document.getElementById(
        "tituloSomenteLeitura"
    );

const textoSomenteLeitura =
    document.getElementById(
        "textoSomenteLeitura"
    );

const linkVoltarDisciplinas =
    document.getElementById(
        "linkVoltarDisciplinas"
    );

const listaAtividades =
    document.getElementById(
        "listaAtividades"
    );

const estadoVazioAtividades =
    document.getElementById(
        "estadoVazioAtividades"
    );

const estadoSemResultado =
    document.getElementById(
        "estadoSemResultado"
    );

const modeloAtividade =
    document.getElementById(
        "modeloAtividade"
    );

const botaoNovaAtividade =
    document.getElementById(
        "botaoNovaAtividade"
    );

const botaoNovaAtividadeVazio =
    document.getElementById(
        "botaoNovaAtividadeVazio"
    );

const buscaAtividade =
    document.getElementById(
        "buscaAtividade"
    );

const filtroTipo =
    document.getElementById(
        "filtroTipo"
    );

const filtroStatus =
    document.getElementById(
        "filtroStatus"
    );

const modalAtividadeElemento =
    document.getElementById(
        "modalAtividade"
    );

const tituloModalAtividade =
    document.getElementById(
        "tituloModalAtividade"
    );

const nomeAtividade =
    document.getElementById(
        "nomeAtividade"
    );

const tipoAtividade =
    document.getElementById(
        "tipoAtividade"
    );

const statusAtividade =
    document.getElementById(
        "statusAtividade"
    );

const descricaoAtividade =
    document.getElementById(
        "descricaoAtividade"
    );

const dataEntregaAtividade =
    document.getElementById(
        "dataEntregaAtividade"
    );

const prioridadeAtividade =
    document.getElementById(
        "prioridadeAtividade"
    );

const notaMaximaAtividade =
    document.getElementById(
        "notaMaximaAtividade"
    );

const notaObtidaAtividade =
    document.getElementById(
        "notaObtidaAtividade"
    );

const pesoAtividade =
    document.getElementById(
        "pesoAtividade"
    );

const observacoesAtividade =
    document.getElementById(
        "observacoesAtividade"
    );

const salvarAtividade =
    document.getElementById(
        "salvarAtividade"
    );

const modalDocumentosElemento =
    document.getElementById(
        "modalDocumentos"
    );

const nomeAtividadeDocumentos =
    document.getElementById(
        "nomeAtividadeDocumentos"
    );

const areaUploadDocumento =
    document.getElementById(
        "areaUploadDocumento"
    );

const arquivoDocumento =
    document.getElementById(
        "arquivoDocumento"
    );

const enviarDocumento =
    document.getElementById(
        "enviarDocumento"
    );

const listaDocumentos =
    document.getElementById(
        "listaDocumentos"
    );

const estadoVazioDocumentos =
    document.getElementById(
        "estadoVazioDocumentos"
    );

const modeloDocumento =
    document.getElementById(
        "modeloDocumento"
    );

let cursoAtual = null;
let semestreAtual = null;
let disciplinaAtual = null;

let atividades = [];

let atividadeEmEdicao = null;
let atividadeDocumentosAtual = null;

let somenteLeitura = false;

function idValido(id) {

    return Number.isInteger(id)
        && id > 0;
}

function formatarData(
    data
) {

    if (!data) {
        return "-";
    }

    const partes =
        String(data)
            .split("-");

    if (partes.length !== 3) {
        return data;
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function formatarDataHora(
    valor
) {

    if (!valor) {
        return "-";
    }

    const data =
        new Date(valor);

    if (Number.isNaN(
        data.getTime()
    )) {

        return valor;
    }

    return data.toLocaleString(
        "pt-BR",
        {
            dateStyle: "short",
            timeStyle: "short"
        }
    );
}

function formatarNumero(
    valor
) {

    if (valor === null
        || valor === undefined
        || valor === "") {

        return "-";
    }

    return Number(valor)
        .toLocaleString(
            "pt-BR",
            {
                maximumFractionDigits: 2
            }
        );
}

function converterNumero(
    valor
) {

    if (valor === null
        || valor === undefined
        || valor === "") {

        return null;
    }

    const numero =
        Number(valor);

    return Number.isNaN(numero)
        ? null
        : numero;
}

function hojeFormatoIso() {

    const hoje =
        new Date();

    const ano =
        hoje.getFullYear();

    const mes =
        String(
            hoje.getMonth() + 1
        ).padStart(
            2,
            "0"
        );

    const dia =
        String(
            hoje.getDate()
        ).padStart(
            2,
            "0"
        );

    return `${ano}-${mes}-${dia}`;
}

function atividadeAtrasada(
    atividade
) {

    if (!atividade.dataDeEntrega) {
        return false;
    }

    if (atividade.status === "CONCLUIDA") {
        return false;
    }

    return atividade.dataDeEntrega <
        hojeFormatoIso();
}

function obterStatusVisual(
    atividade
) {

    if (atividadeAtrasada(
        atividade
    )) {

        return "ATRASADA";
    }

    return atividade.status ||
        "PENDENTE";
}

function formatarStatus(
    status
) {

    const valores = {
        PENDENTE: "Pendente",
        EM_ANDAMENTO: "Em andamento",
        CONCLUIDA: "Concluída",
        ATRASADA: "Atrasada"
    };

    return valores[status]
        || status
        || "-";
}

function formatarTipo(
    tipo
) {

    const valores = {
        ATIVIDADE: "Atividade",
        PROVA: "Prova",
        TRABALHO: "Trabalho",
        OUTRA: "Outra"
    };

    return valores[tipo]
        || tipo
        || "Outra";
}

function formatarPrioridade(
    prioridade
) {

    const valores = {
        BAIXA: "Baixa",
        MEDIA: "Média",
        ALTA: "Alta"
    };

    return valores[prioridade]
        || prioridade
        || "-";
}

function formatarTamanho(
    bytes
) {

    const tamanho =
        Number(bytes);

    if (!Number.isFinite(tamanho)
        || tamanho < 0) {

        return "-";
    }

    if (tamanho < 1024) {

        return `${tamanho} B`;
    }

    if (tamanho < 1024 * 1024) {

        return `${(
            tamanho / 1024
        ).toFixed(1)} KB`;
    }

    return `${(
        tamanho /
        (1024 * 1024)
    ).toFixed(1)} MB`;
}

function obterClasseStatus(
    status
) {

    const classes = {
        PENDENTE:
            "status-pendente",

        EM_ANDAMENTO:
            "status-andamento",

        CONCLUIDA:
            "status-concluida",

        ATRASADA:
            "status-atrasada"
    };

    return classes[status]
        || "status-pendente";
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

            const texto =
                await resposta.text();

            return texto ||
                "Ocorreu um erro.";

        } catch {

            return "Ocorreu um erro.";
        }
    }
}

async function carregarUsuarioTopo() {

    nomeUsuarioLogado.textContent =
        usuarioLogado.nome ||
        "Usuário";

    emailUsuarioLogado.textContent =
        usuarioLogado.email ||
        "";

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

        const usuario =
            dados.usuario ||
            dados;

        if (usuario.nome) {

            nomeUsuarioLogado.textContent =
                usuario.nome;

            usuarioLogado.nome =
                usuario.nome;
        }

        if (usuario.email) {

            emailUsuarioLogado.textContent =
                usuario.email;

            usuarioLogado.email =
                usuario.email;
        }

        localStorage.setItem(
            "usuarioLogado",
            JSON.stringify(
                usuarioLogado
            )
        );

        if (dados.fotoUrl) {

            imagemUsuarioTopo.src =
                dados.fotoUrl;

            imagemUsuarioTopo
                .classList
                .remove(
                    "d-none"
                );

            iconeUsuarioTopo
                .classList
                .add(
                    "d-none"
                );

        } else {

            imagemUsuarioTopo.src =
                "";

            imagemUsuarioTopo
                .classList
                .add(
                    "d-none"
                );

            iconeUsuarioTopo
                .classList
                .remove(
                    "d-none"
                );
        }

    } catch (erro) {

        console.error(
            "Erro ao carregar usuário:",
            erro
        );
    }
}

async function carregarContextoAcademico() {

    const cursos =
        await CursoContexto
            .carregarCursos();

    cursoAtual =
        CursoContexto
            .encontrarCurso(
                cursos,
                idCurso
            );

    if (!cursoAtual) {

        throw new Error(
            "Curso não encontrado."
        );
    }

    CursoContexto
        .salvarCursoSelecionado(
            idCurso
        );

    const respostaSemestres =
        await fetch(
            `${API_URL}/Cursos/${idCurso}/Semestres`,
            {
                headers: {
                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

    if (!respostaSemestres.ok) {

        throw new Error(
            await obterMensagemErro(
                respostaSemestres
            )
        );
    }

    const semestres =
        await respostaSemestres
            .json();

    semestreAtual =
        semestres.find(
            semestre =>
                Number(
                    semestre.idSemestre
                ) === idSemestre
        );

    if (!semestreAtual) {

        throw new Error(
            "Semestre não encontrado."
        );
    }

    const respostaDisciplina =
        await fetch(
            `${API_URL}/Disciplinas/${idDisciplina}`,
            {
                headers: {
                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

    if (!respostaDisciplina.ok) {

        throw new Error(
            await obterMensagemErro(
                respostaDisciplina
            )
        );
    }

    disciplinaAtual =
        await respostaDisciplina
            .json();

    if (
        Number(
            disciplinaAtual.idSemestre
        ) !== idSemestre
    ) {

        throw new Error(
            "A disciplina não pertence ao semestre informado."
        );
    }

    nomeDisciplinaAtual.textContent =
        disciplinaAtual.nome ||
        "Disciplina";

    nomeSemestreAtual.textContent =
        semestreAtual.nome ||
        "Semestre";

    linkVoltarDisciplinas.href =
        `disciplinas.html?idCurso=${idCurso}&idSemestre=${idSemestre}`;

    configurarStatusCurso();
}

function configurarStatusCurso() {

    const status =
        String(
            cursoAtual.status ||
            "ATIVO"
        ).toUpperCase();

    statusCursoAtual.className =
        "badge-status-curso";

    if (status === "TRANCADO") {

        somenteLeitura = true;

        statusCursoAtual.textContent =
            "Trancado";

        statusCursoAtual
            .classList
            .add(
                "trancado"
            );

        avisoSomenteLeitura
            .classList
            .remove(
                "d-none"
            );

        tituloSomenteLeitura.textContent =
            "Curso trancado";

        textoSomenteLeitura.textContent =
            "As atividades e documentos podem ser consultados, mas não alterados.";

    } else if (
        status === "CONCLUIDO"
    ) {

        somenteLeitura = true;

        statusCursoAtual.textContent =
            "Concluído";

        statusCursoAtual
            .classList
            .add(
                "concluido"
            );

        avisoSomenteLeitura
            .classList
            .remove(
                "d-none"
            );

        tituloSomenteLeitura.textContent =
            "Curso concluído";

        textoSomenteLeitura.textContent =
            "O histórico acadêmico permanece disponível para consulta, mas não pode ser alterado.";

    } else {

        somenteLeitura = false;

        statusCursoAtual.textContent =
            "Ativo";

        statusCursoAtual
            .classList
            .add(
                "ativo"
            );

        avisoSomenteLeitura
            .classList
            .add(
                "d-none"
            );
    }

    statusCursoAtual
        .classList
        .remove(
            "d-none"
        );

    botaoNovaAtividade.disabled =
        somenteLeitura;

    botaoNovaAtividadeVazio.disabled =
        somenteLeitura;
}

async function carregarAtividades() {

    try {

        const resposta =
            await fetch(
                `${API_URL}/Disciplinas/${idDisciplina}/Atividades`,
                {
                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );

        if (!resposta.ok) {

            throw new Error(
                await obterMensagemErro(
                    resposta
                )
            );
        }

        const dados =
            await resposta.json();

        atividades =
            Array.isArray(dados)
                ? dados
                : [];

        await renderizarAtividades();

    } catch (erro) {

        console.error(
            erro
        );

        await Swal.fire({
            icon: "error",
            title: "Não foi possível carregar",
            text:
                erro.message ||
                "Não foi possível carregar as atividades."
        });
    }
}

async function buscarDocumentos(
    idAtividade
) {

    const resposta =
        await fetch(
            `${API_URL}/Atividades/${idAtividade}/Documentos`,
            {
                headers: {
                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

    if (!resposta.ok) {

        throw new Error(
            await obterMensagemErro(
                resposta
            )
        );
    }

    const dados =
        await resposta.json();

    return Array.isArray(dados)
        ? dados
        : [];
}

function obterAtividadesFiltradas() {

    const termo =
        buscaAtividade
            .value
            .trim()
            .toLowerCase();

    const tipo =
        filtroTipo.value;

    const status =
        filtroStatus.value;

    return atividades.filter(
        atividade => {

            const correspondeBusca =
                !termo ||
                String(
                    atividade.nome ||
                    ""
                )
                .toLowerCase()
                .includes(
                    termo
                );

            const correspondeTipo =
                !tipo ||
                atividade.tipo === tipo;

            const statusVisual =
                obterStatusVisual(
                    atividade
                );

            const correspondeStatus =
                !status ||
                statusVisual === status;

            return correspondeBusca
                && correspondeTipo
                && correspondeStatus;
        }
    );
}

async function renderizarAtividades() {

    listaAtividades.innerHTML =
        "";

    estadoVazioAtividades
        .classList
        .add(
            "d-none"
        );

    estadoSemResultado
        .classList
        .add(
            "d-none"
        );

    if (atividades.length === 0) {

        estadoVazioAtividades
            .classList
            .remove(
                "d-none"
            );

        return;
    }

    const filtradas =
        obterAtividadesFiltradas();

    if (filtradas.length === 0) {

        estadoSemResultado
            .classList
            .remove(
                "d-none"
            );

        return;
    }

    for (const atividade of filtradas) {

        const fragmento =
            modeloAtividade
                .content
                .cloneNode(
                    true
                );

        const nome =
            fragmento.querySelector(
                ".nome-atividade"
            );

        const descricao =
            fragmento.querySelector(
                ".descricao-atividade"
            );

        const tipo =
            fragmento.querySelector(
                ".tipo-atividade"
            );

        const status =
            fragmento.querySelector(
                ".status-atividade"
            );

        const dataEntrega =
            fragmento.querySelector(
                ".data-entrega"
            );

        const linhaNota =
            fragmento.querySelector(
                ".linha-nota"
            );

        const nota =
            fragmento.querySelector(
                ".nota-atividade"
            );

        const linhaPeso =
            fragmento.querySelector(
                ".linha-peso"
            );

        const peso =
            fragmento.querySelector(
                ".peso-atividade"
            );

        const linhaPrioridade =
            fragmento.querySelector(
                ".linha-prioridade"
            );

        const prioridade =
            fragmento.querySelector(
                ".prioridade-atividade"
            );

        const quantidadeDocumentos =
            fragmento.querySelector(
                ".quantidade-documentos"
            );

const botaoEditar =
    fragmento.querySelector(
        ".botaoEditarAtividade"
    );

const botaoDocumentos =
    fragmento.querySelector(
        ".botaoDocumentosAtividade"
    );

const botaoAlternarConclusao =
    fragmento.querySelector(
        ".botaoAlternarConclusaoAtividade"
    );

const botaoExcluir =
    fragmento.querySelector(
        ".botaoExcluirAtividade"
    );


        nome.textContent =
            atividade.nome ||
            "Atividade";

        descricao.textContent =
            atividade.descricao ||
            "Sem descrição.";

        tipo.textContent =
            formatarTipo(
                atividade.tipo
            );

        const statusVisual =
            obterStatusVisual(
                atividade
            );

        status.textContent =
            formatarStatus(
                statusVisual
            );

        status.classList.add(
            obterClasseStatus(
                statusVisual
            )
        );

        dataEntrega.textContent =
            formatarData(
                atividade.dataDeEntrega
            );

        if (
            atividade.notaMaxima !== null
            && atividade.notaMaxima !== undefined
        ) {

            nota.textContent =
                atividade.notaObtida !== null
                && atividade.notaObtida !== undefined

                    ? `${formatarNumero(
                        atividade.notaObtida
                    )} / ${formatarNumero(
                        atividade.notaMaxima
                    )}`

                    : `- / ${formatarNumero(
                        atividade.notaMaxima
                    )}`;

            linhaNota
                .classList
                .remove(
                    "d-none"
                );
        }

        if (
            atividade.peso !== null
            && atividade.peso !== undefined
        ) {

            peso.textContent =
                formatarNumero(
                    atividade.peso
                );

            linhaPeso
                .classList
                .remove(
                    "d-none"
                );
        }

        if (atividade.prioridade) {

            prioridade.textContent =
                formatarPrioridade(
                    atividade.prioridade
                );

            linhaPrioridade
                .classList
                .remove(
                    "d-none"
                );
        }

        try {

            const documentos =
                await buscarDocumentos(
                    atividade.idAtividade
                );

            quantidadeDocumentos.textContent =
                documentos.length;

        } catch (erro) {

            console.error(
                "Erro ao contar documentos:",
                erro
            );

            quantidadeDocumentos.textContent =
                "-";
        }

      if (somenteLeitura) {

    botaoEditar.classList.add(
        "d-none"
    );

    botaoAlternarConclusao.classList.add(
        "d-none"
    );

    botaoExcluir.classList.add(
        "d-none"
    );

} else {

    botaoEditar.addEventListener(
        "click",
        () => {

            abrirEdicaoAtividade(
                atividade
            );
        }
    );

    if (
        atividade.status ===
        "CONCLUIDA"
    ) {

        botaoAlternarConclusao.innerHTML = `
            <i class="fa-solid fa-arrow-rotate-left me-2"></i>
            Reabrir atividade
        `;

        botaoAlternarConclusao.classList.remove(
            "btn-outline-success"
        );

        botaoAlternarConclusao.classList.add(
            "btn-outline-secondary"
        );

    } else {

        botaoAlternarConclusao.innerHTML = `
            <i class="fa-solid fa-circle-check me-2"></i>
            Concluir atividade
        `;
    }

    botaoAlternarConclusao.addEventListener(
        "click",
        () => {

            alternarConclusaoAtividade(
                atividade
            );
        }
    );

    botaoExcluir.addEventListener(
        "click",
        () => {

            excluirAtividade(
                atividade
            );
        }
    );
}

botaoDocumentos.addEventListener(
    "click",
    () => {

        abrirDocumentos(
            atividade
        );
    }
);

listaAtividades.appendChild(
    fragmento
);

    }
}

function limparModalAtividade() {

    atividadeEmEdicao =
        null;

    tituloModalAtividade.textContent =
        "Nova atividade";

    nomeAtividade.value =
        "";

    tipoAtividade.value =
        "";

    statusAtividade.value =
        "PENDENTE";

    descricaoAtividade.value =
        "";

    dataEntregaAtividade.value =
        "";

    prioridadeAtividade.value =
        "";

    notaMaximaAtividade.value =
        "";

    notaObtidaAtividade.value =
        "";

    pesoAtividade.value =
        "";

    observacoesAtividade.value =
        "";
}

function abrirEdicaoAtividade(
    atividade
) {

    if (somenteLeitura) {
        return;
    }

    atividadeEmEdicao =
        atividade.idAtividade;

    tituloModalAtividade.textContent =
        "Editar atividade";

    nomeAtividade.value =
        atividade.nome ||
        "";

    tipoAtividade.value =
        atividade.tipo ||
        "";

    statusAtividade.value =
        atividade.status ||
        "PENDENTE";

    descricaoAtividade.value =
        atividade.descricao ||
        "";

    dataEntregaAtividade.value =
        atividade.dataDeEntrega ||
        "";

    prioridadeAtividade.value =
        atividade.prioridade ||
        "";

    notaMaximaAtividade.value =
        atividade.notaMaxima ??
        "";

    notaObtidaAtividade.value =
        atividade.notaObtida ??
        "";

    pesoAtividade.value =
        atividade.peso ??
        "";

    observacoesAtividade.value =
        atividade.observacoes ||
        "";

    const modal =
        bootstrap.Modal
            .getOrCreateInstance(
                modalAtividadeElemento
            );

    modal.show();
}

salvarAtividade.addEventListener(
    "click",
    async () => {

        if (somenteLeitura) {
            return;
        }

        const nome =
            nomeAtividade
                .value
                .trim();

        const tipo =
            tipoAtividade.value;

        const status =
            statusAtividade.value;

        const descricao =
            descricaoAtividade
                .value
                .trim();

        const dataEntrega =
            dataEntregaAtividade
                .value;

        const prioridade =
            prioridadeAtividade
                .value;

        const notaMaxima =
            converterNumero(
                notaMaximaAtividade
                    .value
            );

        const notaObtida =
            converterNumero(
                notaObtidaAtividade
                    .value
            );

        const peso =
            converterNumero(
                pesoAtividade
                    .value
            );

        const observacoes =
            observacoesAtividade
                .value
                .trim();

        if (!nome) {

            await Swal.fire({
                icon: "warning",
                title: "Nome obrigatório",
                text: "Informe o nome da atividade."
            });

            return;
        }

        if (
            notaMaxima !== null
            && notaMaxima < 0
        ) {

            await Swal.fire({
                icon: "warning",
                title: "Nota inválida",
                text: "A nota máxima não pode ser negativa."
            });

            return;
        }

        if (
            notaObtida !== null
            && notaObtida < 0
        ) {

            await Swal.fire({
                icon: "warning",
                title: "Nota inválida",
                text: "A nota obtida não pode ser negativa."
            });

            return;
        }

        if (
            notaObtida !== null
            && notaMaxima === null
        ) {

            await Swal.fire({
                icon: "warning",
                title: "Nota máxima necessária",
                text: "Informe a nota máxima antes da nota obtida."
            });

            return;
        }

        if (
            notaObtida !== null
            && notaMaxima !== null
            && notaObtida > notaMaxima
        ) {

            await Swal.fire({
                icon: "warning",
                title: "Nota inválida",
                text: "A nota obtida não pode ser maior que a nota máxima."
            });

            return;
        }

        if (
            peso !== null
            && peso < 0
        ) {

            await Swal.fire({
                icon: "warning",
                title: "Peso inválido",
                text: "O peso não pode ser negativo."
            });

            return;
        }

        const dados = {
            nome: nome,
            tipo:
                tipo || null,
            descricao:
                descricao || null,
            dataDeEntrega:
                dataEntrega || null,
            status:
                status || "PENDENTE",
            notaMaxima:
                notaMaxima,
            notaObtida:
                notaObtida,
            peso:
                peso,
            prioridade:
                prioridade || null,
            observacoes:
                observacoes || null
        };

        salvarAtividade.disabled =
            true;

        try {

            let resposta;

            if (atividadeEmEdicao) {

                resposta =
                    await fetch(
                        `${API_URL}/Atividades/${atividadeEmEdicao}`,
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
                                    dados
                                )
                        }
                    );

            } else {

                resposta =
                    await fetch(
                        `${API_URL}/Disciplinas/${idDisciplina}/Atividades`,
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
                                    dados
                                )
                        }
                    );
            }

            const respostaDados =
                await resposta
                    .json()
                    .catch(
                        () => ({})
                    );

            if (!resposta.ok) {

                throw new Error(
                    respostaDados.mensagem ||
                    "Não foi possível salvar a atividade."
                );
            }

            const editando =
                Boolean(
                    atividadeEmEdicao
                );

            const modal =
                bootstrap.Modal
                    .getInstance(
                        modalAtividadeElemento
                    );

            if (modal) {
                modal.hide();
            }

            limparModalAtividade();

            await Swal.fire({
                icon: "success",

                title:
                    editando
                        ? "Atividade atualizada"
                        : "Atividade cadastrada",

                text:
                    editando
                        ? "A atividade foi atualizada com sucesso."
                        : "A atividade foi cadastrada com sucesso.",

                confirmButtonText:
                    "Continuar"
            });

            await carregarAtividades();

        } catch (erro) {

            console.error(
                erro
            );

            await Swal.fire({
                icon: "error",
                title: "Não foi possível salvar",
                text:
                    erro.message ||
                    "Não foi possível salvar a atividade."
            });

        } finally {

            salvarAtividade.disabled =
                false;
        }
    }
);

async function alternarConclusaoAtividade(
    atividade
) {

    if (somenteLeitura) {
        return;
    }

    const estaConcluida =
        atividade.status ===
        "CONCLUIDA";

    const novoStatus =
        estaConcluida
            ? "PENDENTE"
            : "CONCLUIDA";

    const resultado =
        await Swal.fire({
            icon:
                estaConcluida
                    ? "question"
                    : "success",

            title:
                estaConcluida
                    ? "Reabrir atividade?"
                    : "Concluir atividade?",

            text:
                estaConcluida
                    ? "A atividade voltará para o status Pendente."
                    : "A atividade será marcada como concluída.",

            showCancelButton: true,

            confirmButtonText:
                estaConcluida
                    ? "Reabrir"
                    : "Concluir",

            cancelButtonText:
                "Cancelar",

            reverseButtons: true
        });

    if (!resultado.isConfirmed) {
        return;
    }

    const dados = {
        nome:
            atividade.nome,

        tipo:
            atividade.tipo || null,

        descricao:
            atividade.descricao || null,

        dataDeEntrega:
            atividade.dataDeEntrega || null,

        status:
            novoStatus,

        notaMaxima:
            atividade.notaMaxima ?? null,

        notaObtida:
            atividade.notaObtida ?? null,

        peso:
            atividade.peso ?? null,

        prioridade:
            atividade.prioridade || null,

        observacoes:
            atividade.observacoes || null
    };

    try {

        const resposta =
            await fetch(
                `${API_URL}/Atividades/${atividade.idAtividade}`,
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
                            dados
                        )
                }
            );

        const respostaDados =
            await resposta
                .json()
                .catch(
                    () => ({})
                );

        if (!resposta.ok) {

            throw new Error(
                respostaDados.mensagem ||
                "Não foi possível alterar o status da atividade."
            );
        }

        await Swal.fire({
            icon: "success",

            title:
                estaConcluida
                    ? "Atividade reaberta"
                    : "Atividade concluída",

            text:
                estaConcluida
                    ? "A atividade voltou para Pendente."
                    : "A atividade foi marcada como concluída.",

            timer: 1600,

            showConfirmButton:
                false
        });

        await carregarAtividades();

    } catch (erro) {

        console.error(
            erro
        );

        await Swal.fire({
            icon: "error",
            title: "Não foi possível alterar",
            text:
                erro.message ||
                "Não foi possível alterar o status da atividade."
        });
    }
}

async function excluirAtividade(
    atividade
) {

    if (somenteLeitura) {
        return;
    }

    const resultado =
        await Swal.fire({
            icon: "warning",

            title: "Excluir atividade?",

            html: `
                <p>
                    Você está prestes a excluir
                    <strong>${atividade.nome}</strong>.
                </p>

                <p>
                    Os documentos vinculados à atividade
                    também serão excluídos.
                </p>
            `,

            showCancelButton: true,

            confirmButtonText:
                "Excluir",

            cancelButtonText:
                "Cancelar",

            reverseButtons: true
        });

    if (!resultado.isConfirmed) {
        return;
    }

    try {

        const resposta =
            await fetch(
                `${API_URL}/Atividades/${atividade.idAtividade}`,
                {
                    method: "DELETE",

                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );

        if (!resposta.ok) {

            throw new Error(
                await obterMensagemErro(
                    resposta
                )
            );
        }

        await Swal.fire({
            icon: "success",
            title: "Atividade excluída",
            text: "A atividade e seus documentos foram excluídos com sucesso.",
            confirmButtonText: "Continuar"
        });

        await carregarAtividades();

    } catch (erro) {

        console.error(
            erro
        );

        await Swal.fire({
            icon: "error",
            title: "Não foi possível excluir",
            text:
                erro.message ||
                "Não foi possível excluir a atividade."
        });
    }
}

async function abrirDocumentos(
    atividade
) {

    atividadeDocumentosAtual =
        atividade;

    nomeAtividadeDocumentos.textContent =
        atividade.nome ||
        "";

    arquivoDocumento.value =
        "";

    if (somenteLeitura) {

        areaUploadDocumento
            .classList
            .add(
                "d-none"
            );

    } else {

        areaUploadDocumento
            .classList
            .remove(
                "d-none"
            );
    }

    const modal =
        bootstrap.Modal
            .getOrCreateInstance(
                modalDocumentosElemento
            );

    modal.show();

    await carregarDocumentosModal();
}

async function carregarDocumentosModal() {

    if (!atividadeDocumentosAtual) {
        return;
    }

    listaDocumentos.innerHTML =
        "";

    estadoVazioDocumentos
        .classList
        .add(
            "d-none"
        );

    try {

        const documentos =
            await buscarDocumentos(
                atividadeDocumentosAtual
                    .idAtividade
            );

        if (documentos.length === 0) {

            estadoVazioDocumentos
                .classList
                .remove(
                    "d-none"
                );

            return;
        }

        documentos.forEach(
            documento => {

                const fragmento =
                    modeloDocumento
                        .content
                        .cloneNode(
                            true
                        );

                const nome =
                    fragmento.querySelector(
                        ".nome-documento"
                    );

                const tamanho =
                    fragmento.querySelector(
                        ".tamanho-documento"
                    );

                const data =
                    fragmento.querySelector(
                        ".data-documento"
                    );

                const botaoAbrir =
                    fragmento.querySelector(
                        ".botaoAbrirDocumento"
                    );

                const botaoExcluir =
                    fragmento.querySelector(
                        ".botaoExcluirDocumento"
                    );

                nome.textContent =
                    documento.nomeOriginal ||
                    "Documento";

                tamanho.textContent =
                    formatarTamanho(
                        documento.tamanhoBytes
                    );

                data.textContent =
                    formatarDataHora(
                        documento.dataUpload
                    );

                botaoAbrir.addEventListener(
                    "click",
                    () => {

                        abrirDocumento(
                            documento
                        );
                    }
                );

                if (somenteLeitura) {

                    botaoExcluir
                        .classList
                        .add(
                            "d-none"
                        );

                } else {

                    botaoExcluir
                        .addEventListener(
                            "click",
                            () => {

                                excluirDocumento(
                                    documento
                                );
                            }
                        );
                }

                listaDocumentos.appendChild(
                    fragmento
                );
            }
        );

    } catch (erro) {

        console.error(
            erro
        );

        await Swal.fire({
            icon: "error",
            title: "Não foi possível carregar",
            text:
                erro.message ||
                "Não foi possível carregar os documentos."
        });
    }
}

enviarDocumento.addEventListener(
    "click",
    async () => {

        if (
            somenteLeitura
            || !atividadeDocumentosAtual
        ) {

            return;
        }

        const arquivo =
            arquivoDocumento
                .files[0];

        if (!arquivo) {

            await Swal.fire({
                icon: "warning",
                title: "Selecione um arquivo",
                text: "Escolha o documento que deseja enviar."
            });

            return;
        }

        const tamanhoMaximo =
            10 * 1024 * 1024;

        if (
            arquivo.size >
            tamanhoMaximo
        ) {

            await Swal.fire({
                icon: "warning",
                title: "Arquivo muito grande",
                text: "O arquivo deve possuir no máximo 10 MB."
            });

            return;
        }

        const extensoesPermitidas = [
            "pdf",
            "doc",
            "docx",
            "xls",
            "xlsx",
            "ppt",
            "pptx",
            "png",
            "jpg",
            "jpeg"
        ];

        const extensao =
            arquivo.name
                .split(".")
                .pop()
                .toLowerCase();

        if (
            !extensoesPermitidas
                .includes(
                    extensao
                )
        ) {

            await Swal.fire({
                icon: "warning",
                title: "Formato não permitido",
                text: "Selecione um arquivo PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, PNG, JPG ou JPEG."
            });

            return;
        }

        const formulario =
            new FormData();

        formulario.append(
            "arquivo",
            arquivo
        );

        enviarDocumento.disabled =
            true;

        try {

            const resposta =
                await fetch(
                    `${API_URL}/Atividades/${atividadeDocumentosAtual.idAtividade}/Documentos`,
                    {
                        method: "POST",

                        headers: {
                            "Authorization":
                                `Bearer ${token}`
                        },

                        body:
                            formulario
                    }
                );

            const dados =
                await resposta
                    .json()
                    .catch(
                        () => ({})
                    );

            if (!resposta.ok) {

                throw new Error(
                    dados.mensagem ||
                    "Não foi possível enviar o documento."
                );
            }

            arquivoDocumento.value =
                "";

            await Swal.fire({
                icon: "success",
                title: "Documento enviado",
                text: "O documento foi adicionado à atividade.",
                timer: 1500,
                showConfirmButton: false
            });

            await carregarDocumentosModal();

            await carregarAtividades();

        } catch (erro) {

            console.error(
                erro
            );

            await Swal.fire({
                icon: "error",
                title: "Não foi possível enviar",
                text:
                    erro.message ||
                    "Não foi possível enviar o documento."
            });

        } finally {

            enviarDocumento.disabled =
                false;
        }
    }
);

async function abrirDocumento(
    documento
) {

    if (!atividadeDocumentosAtual) {
        return;
    }

    try {

        const resposta =
            await fetch(
                `${API_URL}/Atividades/${atividadeDocumentosAtual.idAtividade}/Documentos/${documento.idDocumento}/Url`,
                {
                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );

        const dados =
            await resposta
                .json()
                .catch(
                    () => ({})
                );

        if (!resposta.ok) {

            throw new Error(
                dados.mensagem ||
                "Não foi possível abrir o documento."
            );
        }

        if (!dados.url) {

            throw new Error(
                "A URL do documento não foi encontrada."
            );
        }

        window.open(
            dados.url,
            "_blank",
            "noopener,noreferrer"
        );

    } catch (erro) {

        console.error(
            erro
        );

        await Swal.fire({
            icon: "error",
            title: "Não foi possível abrir",
            text:
                erro.message ||
                "Não foi possível abrir o documento."
        });
    }
}

async function excluirDocumento(
    documento
) {

    if (
        somenteLeitura
        || !atividadeDocumentosAtual
    ) {

        return;
    }

    const resultado =
        await Swal.fire({
            icon: "warning",
            title: "Excluir documento?",
            text:
                `Deseja excluir "${documento.nomeOriginal}"?`,
            showCancelButton: true,
            confirmButtonText: "Excluir",
            cancelButtonText: "Cancelar",
            reverseButtons: true
        });

    if (!resultado.isConfirmed) {
        return;
    }

    try {

        const resposta =
            await fetch(
                `${API_URL}/Atividades/${atividadeDocumentosAtual.idAtividade}/Documentos/${documento.idDocumento}`,
                {
                    method: "DELETE",

                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );

        if (!resposta.ok) {

            throw new Error(
                await obterMensagemErro(
                    resposta
                )
            );
        }

        await Swal.fire({
            icon: "success",
            title: "Documento excluído",
            text: "O documento foi excluído com sucesso.",
            timer: 1500,
            showConfirmButton: false
        });

        await carregarDocumentosModal();

        await carregarAtividades();

    } catch (erro) {

        console.error(
            erro
        );

        await Swal.fire({
            icon: "error",
            title: "Não foi possível excluir",
            text:
                erro.message ||
                "Não foi possível excluir o documento."
        });
    }
}

buscaAtividade.addEventListener(
    "input",
    () => {

        renderizarAtividades();
    }
);

filtroTipo.addEventListener(
    "change",
    () => {

        renderizarAtividades();
    }
);

filtroStatus.addEventListener(
    "change",
    () => {

        renderizarAtividades();
    }
);

modalAtividadeElemento
    .addEventListener(
        "hidden.bs.modal",
        () => {

            limparModalAtividade();
        }
    );

modalDocumentosElemento
    .addEventListener(
        "hidden.bs.modal",
        () => {

            atividadeDocumentosAtual =
                null;

            arquivoDocumento.value =
                "";

            listaDocumentos.innerHTML =
                "";

            estadoVazioDocumentos
                .classList
                .add(
                    "d-none"
                );
        }
    );

async function iniciarPagina() {

    if (
        !idValido(idCurso)
        || !idValido(idSemestre)
        || !idValido(idDisciplina)
    ) {

        await Swal.fire({
            icon: "error",
            title: "Informações inválidas",
            text: "Não foi possível identificar o curso, semestre ou disciplina.",
            confirmButtonText: "Voltar"
        });

        window.location.href =
            "cursos.html";

        return;
    }

    try {

        await CursoContexto
            .inicializarSeletorGlobal({
                aoAlterar: curso => {

                    if (!curso) {
                        return;
                    }

                    window.location.href =
                        `semestres.html?idCurso=${curso.idCurso}`;
                }
            });

        await carregarContextoAcademico();

        await carregarUsuarioTopo();

        await carregarAtividades();

    } catch (erro) {

        console.error(
            erro
        );

        await Swal.fire({
            icon: "error",
            title: "Não foi possível abrir a disciplina",
            text:
                erro.message ||
                "Não foi possível carregar as informações acadêmicas.",
            confirmButtonText: "Voltar"
        });

        window.location.href =
            `semestres.html?idCurso=${idCurso}`;
    }
}

iniciarPagina();