function obterIdCursoAtual() {

    const idCurso =
        Number(
            localStorage.getItem(
                "cursoSelecionadoId"
            )
        );

    if (
        Number.isInteger(idCurso) &&
        idCurso > 0
    ) {
        return idCurso;
    }

    return null;
}

function navegarParaCursos() {

    window.location.href =
        "cursos.html";
}

function navegarParaSemestres() {

    const idCurso =
        obterIdCursoAtual();

    if (!idCurso) {

        navegarParaCursos();

        return;
    }

    window.location.href =
        `semestres.html?idCurso=${idCurso}`;
}

async function navegarParaDisciplinas() {

    const idCurso =
        obterIdCursoAtual();

    if (!idCurso) {

        navegarParaCursos();

        return;
    }

    const token =
        localStorage.getItem("token");

    if (!token) {

        window.location.href =
            "index.html";

        return;
    }

    try {

        const resposta =
            await fetch(
                `${API_URL}/Cursos/${idCurso}/Semestres`,
                {
                    method: "GET",
                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );

        if (!resposta.ok) {

            window.location.href =
                `semestres.html?idCurso=${idCurso}`;

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

            window.location.href =
                `semestres.html?idCurso=${idCurso}`;

            return;
        }

        window.location.href =
            `disciplinas.html?idSemestre=${semestreAtivo.idSemestre}&idCurso=${idCurso}`;

    } catch (erro) {

        console.error(
            "Erro ao acessar disciplinas:",
            erro
        );

        window.location.href =
            `semestres.html?idCurso=${idCurso}`;
    }
}

function configurarNavegacaoAcademica() {

    const linkSemestres =
        document.getElementById(
            "linkSemestresNav"
        );

    const linkDisciplinas =
        document.getElementById(
            "linkDisciplinasNav"
        );

    if (linkSemestres) {

        linkSemestres.addEventListener(
            "click",
            event => {

                event.preventDefault();

                navegarParaSemestres();
            }
        );
    }

    if (linkDisciplinas) {

        linkDisciplinas.addEventListener(
            "click",
            async event => {

                event.preventDefault();

                await navegarParaDisciplinas();
            }
        );
    }
}

document.addEventListener(
    "DOMContentLoaded",
    configurarNavegacaoAcademica
);