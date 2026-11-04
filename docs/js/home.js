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

const nomeSemestreAtual =
    document.getElementById(
        "nomeSemestreAtual"
    );

const periodoSemestreAtual =
    document.getElementById(
        "periodoSemestreAtual"
    );

const statusSemestreAtual =
    document.getElementById(
        "statusSemestreAtual"
    );

const linkDisciplinasMenu =
    document.getElementById(
        "linkDisciplinasMenu"
    );

const linkDisciplinasRapido =
    document.getElementById(
        "linkDisciplinasRapido"
    );

const totalDisciplinas =
    document.getElementById(
        "totalDisciplinas"
    );

const totalFaltas =
    document.getElementById(
        "totalFaltas"
    );

const mediaGeral =
    document.getElementById(
        "mediaGeral"
    );

const disciplinasAbaixoMedia =
    document.getElementById(
        "disciplinasAbaixoMedia"
    );

const graficoMedias =
    document.getElementById(
        "graficoMedias"
    );

const graficoFaltas =
    document.getElementById(
        "graficoFaltas"
    );

const estadoVazioMedias =
    document.getElementById(
        "estadoVazioMedias"
    );

const estadoVazioFaltas =
    document.getElementById(
        "estadoVazioFaltas"
    );

const listaResumoDisciplinas =
    document.getElementById(
        "listaResumoDisciplinas"
    );

const estadoVazioDisciplinas =
    document.getElementById(
        "estadoVazioDisciplinas"
    );

let graficoMediasInstancia = null;
let graficoFaltasInstancia = null;

if (nomeUsuario) {

    nomeUsuario.textContent =
        usuarioLogado.nome || "Usuário";
}

if (emailUsuario) {

    emailUsuario.textContent =
        usuarioLogado.email || "";
}

async function carregarUsuarioTopo() {

    try {

        const resposta = await fetch(
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

function formatarData(data) {

    if (!data) {
        return "";
    }

    const partes =
        data.split("-");

    if (partes.length !== 3) {
        return data;
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function formatarMedia(media) {

    if (media === null
        || media === undefined) {

        return "-";
    }

    return Number(media)
        .toFixed(2)
        .replace(".", ",");
}

function configurarLinksDisciplinas(
    semestreAtivo
) {

    if (!semestreAtivo) {
        return;
    }

    const destino =
        `disciplinas.html?idSemestre=${semestreAtivo.idSemestre}`;

    if (linkDisciplinasMenu) {

        linkDisciplinasMenu.href =
            destino;
    }

    if (linkDisciplinasRapido) {

        linkDisciplinasRapido.href =
            destino;
    }
}

async function carregarSemestreAtual() {

    try {

        const resposta = await fetch(
            `${API_URL}/Semestres`,
            {
                method: "GET",

                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!resposta.ok) {

            if (nomeSemestreAtual) {

                nomeSemestreAtual.textContent =
                    "Não foi possível carregar";
            }

            return;
        }

        const semestres =
            await resposta.json();

        const semestreAtivo =
            semestres.find(
                semestre =>
                    semestre.ativo === "S"
            );

        if (!semestreAtivo) {

            if (nomeSemestreAtual) {

                nomeSemestreAtual.textContent =
                    "Nenhum semestre ativo";
            }

            if (periodoSemestreAtual) {

                periodoSemestreAtual.textContent =
                    "Cadastre ou ative um semestre para começar.";
            }

            if (statusSemestreAtual) {

                statusSemestreAtual.textContent =
                    "Sem semestre";
            }

            return;
        }

        if (nomeSemestreAtual) {

            nomeSemestreAtual.textContent =
                `${semestreAtivo.nome} - ${semestreAtivo.ano}`;
        }

        if (periodoSemestreAtual) {

            periodoSemestreAtual.textContent =
                `${formatarData(
                    semestreAtivo.dataInicio
                )} até ${formatarData(
                    semestreAtivo.dataFim
                )}`;
        }

        if (statusSemestreAtual) {

            statusSemestreAtual.textContent =
                "Ativo";
        }

        configurarLinksDisciplinas(
            semestreAtivo
        );

    } catch (erro) {

        console.error(
            "Erro ao carregar semestre atual:",
            erro
        );

        if (nomeSemestreAtual) {

            nomeSemestreAtual.textContent =
                "Não foi possível carregar";
        }
    }
}

async function carregarDashboard() {

    try {

        const resposta = await fetch(
            `${API_URL}/Dashboard`,
            {
                method: "GET",

                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        const dados =
            await resposta
                .json()
                .catch(() => ({}));

        if (!resposta.ok) {

            throw new Error(
                dados.mensagem ||
                "Não foi possível carregar o Dashboard."
            );
        }

        atualizarBigNumbers(
            dados
        );

        renderizarGraficoMedias(
            dados.disciplinas || []
        );

        renderizarGraficoFaltas(
            dados.disciplinas || []
        );

        renderizarResumoDisciplinas(
            dados.disciplinas || []
        );

    } catch (erro) {

        console.error(
            "Erro ao carregar Dashboard:",
            erro
        );

        definirIndicadoresComoErro();
    }
}

function atualizarBigNumbers(
    dados
) {

    if (totalDisciplinas) {

        totalDisciplinas.textContent =
            dados.totalDisciplinas ?? 0;
    }

    if (totalFaltas) {

        totalFaltas.textContent =
            dados.totalFaltas ?? 0;
    }

    if (mediaGeral) {

        mediaGeral.textContent =
            dados.mediaGeral !== null
                && dados.mediaGeral !== undefined
                ? formatarMedia(
                    dados.mediaGeral
                )
                : "-";
    }

    if (disciplinasAbaixoMedia) {

        disciplinasAbaixoMedia.textContent =
            dados.disciplinasAbaixoMedia ?? 0;
    }
}

function definirIndicadoresComoErro() {

    if (totalDisciplinas) {
        totalDisciplinas.textContent = "-";
    }

    if (totalFaltas) {
        totalFaltas.textContent = "-";
    }

    if (mediaGeral) {
        mediaGeral.textContent = "-";
    }

    if (disciplinasAbaixoMedia) {
        disciplinasAbaixoMedia.textContent = "-";
    }
}

function renderizarGraficoMedias(
    disciplinas
) {

    const disciplinasComMedia =
        disciplinas.filter(
            disciplina =>
                disciplina.mediaFinal !== null
                && disciplina.mediaFinal !== undefined
        );

    if (graficoMediasInstancia) {

        graficoMediasInstancia.destroy();

        graficoMediasInstancia = null;
    }

    if (disciplinasComMedia.length === 0) {

        if (graficoMedias) {

            graficoMedias.classList.add(
                "d-none"
            );
        }

        if (estadoVazioMedias) {

            estadoVazioMedias.classList.remove(
                "d-none"
            );
        }

        return;
    }

    if (graficoMedias) {

        graficoMedias.classList.remove(
            "d-none"
        );
    }

    if (estadoVazioMedias) {

        estadoVazioMedias.classList.add(
            "d-none"
        );
    }

    const nomes =
        disciplinasComMedia.map(
            disciplina =>
                disciplina.nome
        );

    const medias =
        disciplinasComMedia.map(
            disciplina =>
                Number(
                    disciplina.mediaFinal
                )
        );

    const mediasMinimas =
        disciplinasComMedia.map(
            disciplina =>
                disciplina.mediaMinima !== null
                && disciplina.mediaMinima !== undefined
                    ? Number(
                        disciplina.mediaMinima
                    )
                    : 0
        );

    graficoMediasInstancia =
        new Chart(
            graficoMedias,
            {
                type: "bar",

                data: {
                    labels: nomes,

                    datasets: [
                        {
                            label: "Média final",
                            data: medias,
                            backgroundColor:
                                "rgba(37, 99, 235, 0.75)",
                            borderColor:
                                "rgba(37, 99, 235, 1)",
                            borderWidth: 1,
                            borderRadius: 6
                        },
                        {
                            label: "Média mínima",
                            data: mediasMinimas,
                            backgroundColor:
                                "rgba(148, 163, 184, 0.45)",
                            borderColor:
                                "rgba(100, 116, 139, 0.8)",
                            borderWidth: 1,
                            borderRadius: 6
                        }
                    ]
                },

                options: {
                    responsive: true,
                    maintainAspectRatio: false,

                    scales: {
                        y: {
                            beginAtZero: true,
                            max: 10,
                            ticks: {
                                stepSize: 2
                            }
                        }
                    },

                    plugins: {
                        legend: {
                            position: "bottom"
                        }
                    }
                }
            }
        );
}
function renderizarGraficoFaltas(
    disciplinas
) {

    if (graficoFaltasInstancia) {

        graficoFaltasInstancia.destroy();

        graficoFaltasInstancia = null;
    }

    const totalFaltasRegistradas =
        disciplinas.reduce(
            (total, disciplina) =>
                total + (disciplina.faltas ?? 0),
            0
        );

    if (
        disciplinas.length === 0
        || totalFaltasRegistradas === 0
    ) {

        if (graficoFaltas) {

            graficoFaltas.classList.add(
                "d-none"
            );
        }

        if (estadoVazioFaltas) {

            estadoVazioFaltas.classList.remove(
                "d-none"
            );
        }

        return;
    }

    if (graficoFaltas) {

        graficoFaltas.classList.remove(
            "d-none"
        );
    }

    if (estadoVazioFaltas) {

        estadoVazioFaltas.classList.add(
            "d-none"
        );
    }

    const nomes =
        disciplinas.map(
            disciplina =>
                disciplina.nome
        );

    const faltas =
        disciplinas.map(
            disciplina =>
                disciplina.faltas ?? 0
        );

    graficoFaltasInstancia =
        new Chart(
            graficoFaltas,
            {
                type: "doughnut",

                data: {
                    labels: nomes,

                    datasets: [
                        {
                            label: "Faltas",
                            data: faltas,
                            backgroundColor: [
                                "#2563eb",
                                "#7c3aed",
                                "#0891b2",
                                "#16a34a",
                                "#ca8a04",
                                "#ea580c",
                                "#dc2626",
                                "#475569"
                            ],
                            borderWidth: 2
                        }
                    ]
                },

                options: {
                    responsive: true,
                    maintainAspectRatio: false,

                    plugins: {
                        legend: {
                            position: "bottom"
                        }
                    }
                }
            }
        );
}
function renderizarResumoDisciplinas(
    disciplinas
) {

    if (!listaResumoDisciplinas) {
        return;
    }

    listaResumoDisciplinas.innerHTML =
        "";

    if (disciplinas.length === 0) {

        listaResumoDisciplinas.classList.add(
            "d-none"
        );

        if (estadoVazioDisciplinas) {

            estadoVazioDisciplinas
                .classList
                .remove("d-none");
        }

        return;
    }

    listaResumoDisciplinas.classList.remove(
        "d-none"
    );

    if (estadoVazioDisciplinas) {

        estadoVazioDisciplinas
            .classList
            .add("d-none");
    }

    disciplinas.forEach(
        disciplina => {

            const item =
                document.createElement(
                    "div"
                );

            item.className =
                "item-resumo-disciplina";

            const possuiMedia =
                disciplina.mediaFinal !== null
                && disciplina.mediaFinal !== undefined;

            const possuiMediaMinima =
                disciplina.mediaMinima !== null
                && disciplina.mediaMinima !== undefined;

            const abaixoDaMedia =
                possuiMedia
                && possuiMediaMinima
                && Number(
                    disciplina.mediaFinal
                ) <
                Number(
                    disciplina.mediaMinima
                );

            const professor =
                disciplina.professor
                    ? disciplina.professor
                    : "Não informado";

            const mediaFinalTexto =
                possuiMedia
                    ? formatarMedia(
                        disciplina.mediaFinal
                    )
                    : "Não informada";

            const mediaMinimaTexto =
                possuiMediaMinima
                    ? formatarMedia(
                        disciplina.mediaMinima
                    )
                    : "Não informada";

            item.innerHTML = `
                <div class="resumo-disciplina-principal">

                    <div>

                        <h3>
                            ${disciplina.nome}
                        </h3>

                        <p>
                            <i class="fa-solid fa-chalkboard-user"></i>
                            ${professor}
                        </p>

                    </div>

                    ${
                        abaixoDaMedia
                            ? `
                                <span class="badge-desempenho badge-abaixo">
                                    Abaixo da média
                                </span>
                            `
                            : possuiMedia
                                && possuiMediaMinima
                                ? `
                                    <span class="badge-desempenho badge-ok">
                                        Na média
                                    </span>
                                `
                                : ""
                    }

                </div>

                <div class="resumo-disciplina-dados">

                    <span>
                        <i class="fa-solid fa-chart-line"></i>
                        Média:
                        <strong>
                            ${mediaFinalTexto}
                        </strong>
                    </span>

                    <span>
                        <i class="fa-solid fa-bullseye"></i>
                        Mínima:
                        <strong>
                            ${mediaMinimaTexto}
                        </strong>
                    </span>

                    <span>
                        <i class="fa-solid fa-user-clock"></i>
                        Faltas:
                        <strong>
                            ${disciplina.faltas ?? 0}
                        </strong>
                    </span>

                </div>
            `;

            listaResumoDisciplinas.appendChild(
                item
            );
        }
    );
}

async function iniciarDashboard() {

    await carregarUsuarioTopo();

    await Promise.all([
        carregarSemestreAtual(),
        carregarDashboard()
    ]);
}

iniciarDashboard();