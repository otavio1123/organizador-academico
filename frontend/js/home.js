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
            "http://localhost:8080/usuarios/perfil",
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

async function carregarSemestreAtual() {

    try {

        const resposta = await fetch(
            "http://localhost:8080/Semestres",
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

carregarUsuarioTopo();
carregarSemestreAtual();