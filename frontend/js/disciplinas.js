const token = localStorage.getItem("token");
const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));

if (!token || !usuarioLogado) {
    window.location.href = "index.html";
}

const parametros = new URLSearchParams(window.location.search);
const idSemestre = parametros.get("idSemestre");

if (!idSemestre) {
    Swal.fire({
        icon: "error",
        title: "Semestre não informado",
        text: "Não foi possível identificar o semestre selecionado.",
        confirmButtonText: "Voltar"
    }).then(() => {
        window.location.href = "semestres.html";
    });
}

const listaDisciplinas =
    document.getElementById("listaDisciplinas");

const estadoVazioDisciplinas =
    document.getElementById("estadoVazioDisciplinas");

const modeloDisciplina =
    document.getElementById("modeloDisciplina");

const nomeSemestreAtual =
    document.getElementById("nomeSemestreAtual");

const periodoSemestreAtual =
    document.getElementById("periodoSemestreAtual");

const statusSemestre =
    document.getElementById("statusSemestre");

const nomeSemestreModal =
    document.getElementById("nomeSemestreModal");

const nomeDisciplina =
    document.getElementById("nomeDisciplina");

const nomeProfessor =
    document.getElementById("nomeProfessor");

const faltasDisciplina =
    document.getElementById("faltasDisciplina");

const mediaMinima =
    document.getElementById("mediaMinima");

const mediaFinal =
    document.getElementById("mediaFinal");

const salvarDisciplina =
    document.getElementById("salvarDisciplina");

const tituloModalDisciplina =
    document.getElementById("tituloModalDisciplina");

const nomeUsuarioLogado =
    document.getElementById("nomeUsuarioLogado");

const emailUsuarioLogado =
    document.getElementById("emailUsuarioLogado");

const imagemUsuarioTopo =
    document.getElementById("imagemUsuarioTopo");

const iconeUsuarioTopo =
    document.getElementById("iconeUsuarioTopo");

const adicionarHorario =
    document.getElementById("adicionarHorario");

const listaHorariosFormulario =
    document.getElementById("listaHorariosFormulario");

const modeloHorarioFormulario =
    document.getElementById("modeloHorarioFormulario");

let disciplinaEmEdicao = null;
let dadosSemestreAtual = null;
let horariosOriginais = [];

function formatarData(data) {

    if (!data) {
        return "-";
    }

    const partes = data.split("-");

    if (partes.length !== 3) {
        return data;
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function formatarDiaSemana(dia) {

    const dias = {
        SEGUNDA: "Segunda-feira",
        TERCA: "Terça-feira",
        QUARTA: "Quarta-feira",
        QUINTA: "Quinta-feira",
        SEXTA: "Sexta-feira",
        SABADO: "Sábado",
        DOMINGO: "Domingo"
    };

    return dias[dia] || dia || "-";
}

function formatarHorario(hora) {

    if (!hora) {
        return "-";
    }

    return hora.substring(0, 5);
}

function converterNumero(valor) {

    if (valor === null
        || valor === undefined
        || valor === "") {

        return null;
    }

    return Number(valor);
}

async function carregarUsuarioTopo() {

    nomeUsuarioLogado.textContent =
        usuarioLogado.nome || "Usuário";

    emailUsuarioLogado.textContent =
        usuarioLogado.email || "";

    try {

        const resposta = await fetch(
            `${API_URL}/usuarios/perfil`,
            {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        if (!resposta.ok) {
            return;
        }

        const dados = await resposta.json();

        if (dados.nome) {
            nomeUsuarioLogado.textContent = dados.nome;
        }

        if (dados.email) {
            emailUsuarioLogado.textContent = dados.email;
        }

        if (dados.fotoUrl) {

            imagemUsuarioTopo.src = dados.fotoUrl;

            imagemUsuarioTopo.classList.remove("d-none");

            iconeUsuarioTopo.classList.add("d-none");

        } else {

            imagemUsuarioTopo.classList.add("d-none");

            iconeUsuarioTopo.classList.remove("d-none");
        }

    } catch (erro) {

        console.error(
            "Erro ao carregar usuário:",
            erro
        );
    }
}

async function carregarSemestre() {

    try {

        const resposta = await fetch(
            `${API_URL}/Semestres`,
            {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        if (!resposta.ok) {

            throw new Error(
                "Não foi possível carregar o semestre."
            );
        }

        const semestres =
            await resposta.json();

        dadosSemestreAtual =
            semestres.find(
                semestre =>
                    Number(semestre.idSemestre) ===
                    Number(idSemestre)
            );

        if (!dadosSemestreAtual) {

            await Swal.fire({
                icon: "error",
                title: "Semestre não encontrado",
                text: "O semestre selecionado não foi encontrado.",
                confirmButtonText: "Voltar"
            });

            window.location.href =
                "semestres.html";

            return;
        }

        nomeSemestreAtual.textContent =
            dadosSemestreAtual.nome;

        nomeSemestreModal.textContent =
            dadosSemestreAtual.nome;

        periodoSemestreAtual.textContent =
            `${formatarData(
                dadosSemestreAtual.dataInicio
            )} até ${formatarData(
                dadosSemestreAtual.dataFim
            )}`;

        statusSemestre.classList.remove(
            "d-none"
        );

        if (dadosSemestreAtual.ativo === "S") {

            statusSemestre.textContent =
                "Ativo";

        } else {

            statusSemestre.textContent =
                "Inativo";

            statusSemestre.style.backgroundColor =
                "#e2e8f0";

            statusSemestre.style.color =
                "#475569";
        }

    } catch (erro) {

        console.error(erro);

        Swal.fire({
            icon: "error",
            title: "Erro ao carregar semestre",
            text: "Não foi possível carregar as informações do semestre."
        });
    }
}

async function buscarHorariosDisciplina(
    idDisciplina
) {

    try {

        const resposta = await fetch(
            `${API_URL}/Disciplinas/${idDisciplina}/Horarios`,
            {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        if (!resposta.ok) {
            return [];
        }

        return await resposta.json();

    } catch (erro) {

        console.error(
            "Erro ao carregar horários:",
            erro
        );

        return [];
    }
}

async function carregarDisciplinas() {

    try {

        const resposta = await fetch(
            `${API_URL}/Semestres/${idSemestre}/Disciplinas`,
            {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        if (!resposta.ok) {

            const dados =
                await resposta
                    .json()
                    .catch(() => ({}));

            throw new Error(
                dados.mensagem ||
                "Não foi possível carregar as disciplinas."
            );
        }

        const disciplinas =
            await resposta.json();

        await renderizarDisciplinas(
            disciplinas
        );

    } catch (erro) {

        console.error(erro);

        Swal.fire({
            icon: "error",
            title: "Erro ao carregar disciplinas",
            text: erro.message
        });
    }
}

async function renderizarDisciplinas(
    disciplinas
) {

    listaDisciplinas.innerHTML = "";

    if (!disciplinas
        || disciplinas.length === 0) {

        listaDisciplinas.classList.add(
            "d-none"
        );

        estadoVazioDisciplinas.classList.remove(
            "d-none"
        );

        return;
    }

    listaDisciplinas.classList.remove(
        "d-none"
    );

    estadoVazioDisciplinas.classList.add(
        "d-none"
    );

    for (const disciplina of disciplinas) {

        const fragmento =
            modeloDisciplina.content.cloneNode(
                true
            );

        const nome =
            fragmento.querySelector(
                ".nome-disciplina"
            );

        const linhaProfessor =
            fragmento.querySelector(
                ".linha-professor"
            );

        const professor =
            fragmento.querySelector(
                ".nome-professor"
            );

        const quantidadeFaltas =
            fragmento.querySelector(
                ".quantidade-faltas"
            );

        const linhaMediaMinima =
            fragmento.querySelector(
                ".linha-media-minima"
            );

        const valorMediaMinima =
            fragmento.querySelector(
                ".media-minima"
            );

        const linhaMediaFinal =
            fragmento.querySelector(
                ".linha-media-final"
            );

        const valorMediaFinal =
            fragmento.querySelector(
                ".media-final"
            );

        const listaHorarios =
            fragmento.querySelector(
                ".lista-horarios"
            );

        const botaoEditar =
            fragmento.querySelector(
                ".botaoEditarDisciplina"
            );

        const botaoExcluir =
            fragmento.querySelector(
                ".botaoExcluirDisciplina"
            );

        const botaoAtividades =
            fragmento.querySelector(
                ".botaoVerAtividades"
            );

        nome.textContent =
            disciplina.nome;

        if (disciplina.nomeProfessor) {

            professor.textContent =
                disciplina.nomeProfessor;

            linhaProfessor.classList.remove(
                "d-none"
            );
        }

        quantidadeFaltas.textContent =
            disciplina.faltas ?? 0;

        if (disciplina.mediaMinima !== null
            && disciplina.mediaMinima !== undefined) {

            valorMediaMinima.textContent =
                Number(
                    disciplina.mediaMinima
                ).toFixed(2);

            linhaMediaMinima.classList.remove(
                "d-none"
            );
        }

        if (disciplina.mediaFinal !== null
            && disciplina.mediaFinal !== undefined) {

            valorMediaFinal.textContent =
                Number(
                    disciplina.mediaFinal
                ).toFixed(2);

            linhaMediaFinal.classList.remove(
                "d-none"
            );
        }

        const horarios =
            await buscarHorariosDisciplina(
                disciplina.idDisciplina
            );

        horarios.forEach(
            horario => {

                const paragrafo =
                    document.createElement("p");

                paragrafo.innerHTML = `
                    <i class="fa-regular fa-clock me-2"></i>
                    <strong>${formatarDiaSemana(
                        horario.diaSemana
                    )}:</strong>
                    ${formatarHorario(
                        horario.horaInicio
                    )}
                    às
                    ${formatarHorario(
                        horario.horaFim
                    )}
                `;

                listaHorarios.appendChild(
                    paragrafo
                );
            }
        );

        botaoEditar.addEventListener(
            "click",
            () => {

                abrirEdicao(
                    disciplina
                );
            }
        );

        botaoExcluir.addEventListener(
            "click",
            () => {

                excluirDisciplina(
                    disciplina
                );
            }
        );

        botaoAtividades.href =
            `atividades.html?idDisciplina=${disciplina.idDisciplina}`;

        listaDisciplinas.appendChild(
            fragmento
        );
    }
}

function adicionarLinhaHorario(
    horario = null
) {

    const fragmento =
        modeloHorarioFormulario
            .content
            .cloneNode(true);

    const linha =
        fragmento.querySelector(
            ".linha-horario"
        );

    const idHorario =
        fragmento.querySelector(
            ".id-horario"
        );

    const diaSemana =
        fragmento.querySelector(
            ".dia-semana"
        );

    const horaInicio =
        fragmento.querySelector(
            ".hora-inicio"
        );

    const horaFim =
        fragmento.querySelector(
            ".hora-fim"
        );

    const remover =
        fragmento.querySelector(
            ".remover-horario"
        );

    if (horario) {

        idHorario.value =
            horario.idHorario || "";

        diaSemana.value =
            horario.diaSemana || "";

        horaInicio.value =
            formatarHorario(
                horario.horaInicio
            );

        horaFim.value =
            formatarHorario(
                horario.horaFim
            );
    }

    remover.addEventListener(
        "click",
        () => {

            linha.remove();
        }
    );

    listaHorariosFormulario.appendChild(
        fragmento
    );
}

async function abrirEdicao(
    disciplina
) {

    disciplinaEmEdicao =
        disciplina.idDisciplina;

    tituloModalDisciplina.textContent =
        "Editar disciplina";

    nomeDisciplina.value =
        disciplina.nome || "";

    nomeProfessor.value =
        disciplina.nomeProfessor || "";

    faltasDisciplina.value =
        disciplina.faltas ?? 0;

    mediaMinima.value =
        disciplina.mediaMinima ?? "";

    mediaFinal.value =
        disciplina.mediaFinal ?? "";

    nomeSemestreModal.textContent =
        dadosSemestreAtual
            ? dadosSemestreAtual.nome
            : "-";

    listaHorariosFormulario.innerHTML =
        "";

    horariosOriginais =
        await buscarHorariosDisciplina(
            disciplina.idDisciplina
        );

    horariosOriginais.forEach(
        horario => {

            adicionarLinhaHorario(
                horario
            );
        }
    );

    const modal =
        bootstrap.Modal.getOrCreateInstance(
            document.getElementById(
                "modalDisciplina"
            )
        );

    modal.show();
}

function limparModal() {

    disciplinaEmEdicao = null;

    horariosOriginais = [];

    tituloModalDisciplina.textContent =
        "Nova disciplina";

    nomeDisciplina.value = "";

    nomeProfessor.value = "";

    faltasDisciplina.value = "0";

    mediaMinima.value = "";

    mediaFinal.value = "";

    listaHorariosFormulario.innerHTML =
        "";

    nomeSemestreModal.textContent =
        dadosSemestreAtual
            ? dadosSemestreAtual.nome
            : "-";
}

function obterHorariosFormulario() {

    const linhas =
        listaHorariosFormulario
            .querySelectorAll(
                ".linha-horario"
            );

    return Array.from(linhas)
        .map(
            linha => {

                return {
                    idHorario:
                        linha.querySelector(
                            ".id-horario"
                        ).value,

                    diaSemana:
                        linha.querySelector(
                            ".dia-semana"
                        ).value,

                    horaInicio:
                        linha.querySelector(
                            ".hora-inicio"
                        ).value,

                    horaFim:
                        linha.querySelector(
                            ".hora-fim"
                        ).value
                };
            }
        );
}

function validarHorarios(
    horarios
) {

    for (const horario of horarios) {

        if (!horario.diaSemana) {

            return "Selecione o dia da semana de todos os horários.";
        }

        if (horario.horaInicio
            && horario.horaFim
            && horario.horaFim <= horario.horaInicio) {

            return "O horário final deve ser posterior ao horário inicial.";
        }
    }

    return null;
}

async function salvarHorarios(
    idDisciplina
) {

    const horariosAtuais =
        obterHorariosFormulario();

    if (disciplinaEmEdicao) {

        const idsAtuais =
            horariosAtuais
                .filter(
                    horario =>
                        horario.idHorario
                )
                .map(
                    horario =>
                        Number(
                            horario.idHorario
                        )
                );

        const removidos =
            horariosOriginais.filter(
                horario =>
                    !idsAtuais.includes(
                        Number(
                            horario.idHorario
                        )
                    )
            );

        for (const horario of removidos) {

            const resposta = await fetch(
                `${API_URL}/HorariosDisciplina/${horario.idHorario}`,
                {
                    method: "DELETE",
                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );

            if (!resposta.ok) {

                const dados =
                    await resposta
                        .json()
                        .catch(() => ({}));

                throw new Error(
                    dados.mensagem ||
                    "Não foi possível remover um horário."
                );
            }
        }
    }

    const novosHorarios =
        horariosAtuais.filter(
            horario =>
                !horario.idHorario
        );

    for (const horario of novosHorarios) {

        const resposta = await fetch(
            `${API_URL}/Disciplinas/${idDisciplina}/Horarios`,
            {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/json",
                    "Authorization":
                        `Bearer ${token}`
                },
                body: JSON.stringify({
                    diaSemana:
                        horario.diaSemana,
                    horaInicio:
                        horario.horaInicio || null,
                    horaFim:
                        horario.horaFim || null
                })
            }
        );

        if (!resposta.ok) {

            const dados =
                await resposta
                    .json()
                    .catch(() => ({}));

            throw new Error(
                dados.mensagem ||
                "Não foi possível salvar um horário."
            );
        }
    }
}

adicionarHorario.addEventListener(
    "click",
    () => {

        adicionarLinhaHorario();
    }
);

salvarDisciplina.addEventListener(
    "click",
    async () => {

        const nome =
            nomeDisciplina
                .value
                .trim();

        const professor =
            nomeProfessor
                .value
                .trim();

        const faltas =
            converterNumero(
                faltasDisciplina.value
            );

        const minima =
            converterNumero(
                mediaMinima.value
            );

        const final =
            converterNumero(
                mediaFinal.value
            );

        if (!nome) {

            Swal.fire({
                icon: "warning",
                title: "Nome obrigatório",
                text: "Informe o nome da disciplina."
            });

            return;
        }

        if (faltas !== null
            && faltas < 0) {

            Swal.fire({
                icon: "warning",
                title: "Faltas inválidas",
                text: "A quantidade de faltas não pode ser negativa."
            });

            return;
        }

        if (minima !== null
            && (minima < 0 || minima > 10)) {

            Swal.fire({
                icon: "warning",
                title: "Média inválida",
                text: "A média mínima deve estar entre 0 e 10."
            });

            return;
        }

        if (final !== null
            && (final < 0 || final > 10)) {

            Swal.fire({
                icon: "warning",
                title: "Média inválida",
                text: "A média final deve estar entre 0 e 10."
            });

            return;
        }

        const horarios =
            obterHorariosFormulario();

        const erroHorario =
            validarHorarios(
                horarios
            );

        if (erroHorario) {

            Swal.fire({
                icon: "warning",
                title: "Horário inválido",
                text: erroHorario
            });

            return;
        }

        salvarDisciplina.disabled =
            true;

        try {

            let resposta;

            const dadosDisciplina = {
                nome: nome,
                nomeProfessor:
                    professor || null,
                faltas:
                    faltas ?? 0,
                mediaMinima:
                    minima,
                mediaFinal:
                    final
            };

            if (disciplinaEmEdicao) {

                resposta = await fetch(
                    `${API_URL}/Disciplinas/${disciplinaEmEdicao}`,
                    {
                        method: "PUT",
                        headers: {
                            "Content-Type":
                                "application/json",
                            "Authorization":
                                `Bearer ${token}`
                        },
                        body: JSON.stringify(
                            dadosDisciplina
                        )
                    }
                );

            } else {

                resposta = await fetch(
                    `${API_URL}/Semestres/${idSemestre}/Disciplinas`,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type":
                                "application/json",
                            "Authorization":
                                `Bearer ${token}`
                        },
                        body: JSON.stringify(
                            dadosDisciplina
                        )
                    }
                );
            }

            const dados =
                await resposta
                    .json()
                    .catch(() => ({}));

            if (!resposta.ok) {

                throw new Error(
                    dados.mensagem ||
                    "Não foi possível salvar a disciplina."
                );
            }

            const idDisciplinaSalva =
                disciplinaEmEdicao
                    ? disciplinaEmEdicao
                    : dados.idDisciplina;

            await salvarHorarios(
                idDisciplinaSalva
            );

            const estavaEditando =
                Boolean(
                    disciplinaEmEdicao
                );

            const modal =
                bootstrap.Modal.getInstance(
                    document.getElementById(
                        "modalDisciplina"
                    )
                );

            if (modal) {
                modal.hide();
            }

            await Swal.fire({
                icon: "success",
                title: estavaEditando
                    ? "Disciplina atualizada"
                    : "Disciplina cadastrada",
                text: estavaEditando
                    ? "A disciplina foi atualizada com sucesso."
                    : "A disciplina foi cadastrada com sucesso.",
                confirmButtonText: "Continuar"
            });

            limparModal();

            await carregarDisciplinas();

        } catch (erro) {

            console.error(erro);

            Swal.fire({
                icon: "error",
                title: "Não foi possível salvar",
                text: erro.message
            });

        } finally {

            salvarDisciplina.disabled =
                false;
        }
    }
);

async function excluirDisciplina(
    disciplina
) {

    const resultado =
        await Swal.fire({
            icon: "warning",
            title: "Excluir disciplina?",
            html: `
                <p>
                    Você está prestes a excluir
                    <strong>${disciplina.nome}</strong>.
                </p>

                <p>
                    Os horários e as atividades vinculadas
                    também poderão ser excluídos.
                </p>
            `,
            showCancelButton: true,
            confirmButtonText: "Excluir",
            cancelButtonText: "Cancelar",
            reverseButtons: true
        });

    if (!resultado.isConfirmed) {
        return;
    }

    try {

        const resposta = await fetch(
            `${API_URL}/Disciplinas/${disciplina.idDisciplina}`,
            {
                method: "DELETE",
                headers: {
                    "Authorization":
                        `Bearer ${token}`
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
                "Não foi possível excluir a disciplina."
            );
        }

        await Swal.fire({
            icon: "success",
            title: "Disciplina excluída",
            text: "A disciplina foi excluída com sucesso.",
            confirmButtonText: "Continuar"
        });

        await carregarDisciplinas();

    } catch (erro) {

        Swal.fire({
            icon: "error",
            title: "Não foi possível excluir",
            text: erro.message
        });
    }
}

document
    .getElementById("modalDisciplina")
    .addEventListener(
        "hidden.bs.modal",
        () => {

            limparModal();
        }
    );

carregarUsuarioTopo();
carregarSemestre();
carregarDisciplinas();