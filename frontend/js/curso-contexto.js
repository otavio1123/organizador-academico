(function () {
    const CHAVE_CURSO = "cursoSelecionadoId";

    function normalizarIdCurso(valor) {
        const idCurso = Number(valor);

        return Number.isInteger(idCurso) && idCurso > 0
            ? idCurso
            : null;
    }

    function obterIdCursoUrl() {
        const parametros = new URLSearchParams(
            window.location.search
        );

        return normalizarIdCurso(
            parametros.get("idCurso")
        );
    }

    function obterCursoSelecionadoId() {
        return normalizarIdCurso(
            localStorage.getItem(CHAVE_CURSO)
        );
    }

    function salvarCursoSelecionado(idCurso) {
        const idNormalizado = normalizarIdCurso(idCurso);

        if (!idNormalizado) {
            return null;
        }

        localStorage.setItem(
            CHAVE_CURSO,
            String(idNormalizado)
        );

        return idNormalizado;
    }

    function limparCursoSelecionado() {
        localStorage.removeItem(CHAVE_CURSO);
    }

    async function carregarCursos() {
        const token = localStorage.getItem("token");
        const resposta = await fetch(
            `${API_URL}/Cursos`,
            {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        const dados = await resposta
            .json()
            .catch(() => ({}));

        if (!resposta.ok) {
            throw new Error(
                dados.mensagem ||
                "Não foi possível carregar os cursos."
            );
        }

        return Array.isArray(dados) ? dados : [];
    }

    function encontrarCurso(cursos, idCurso) {
        const idNormalizado = normalizarIdCurso(idCurso);

        if (!idNormalizado) {
            return null;
        }

        return cursos.find(
            curso => Number(curso.idCurso) === idNormalizado
        ) || null;
    }

    async function resolverCursoAtual() {
        const cursos = await carregarCursos();
        const idCursoUrl = obterIdCursoUrl();
        const idCursoSalvo = obterCursoSelecionadoId();

        let cursoSelecionado = encontrarCurso(
            cursos,
            idCursoUrl
        );

        if (!cursoSelecionado) {
            cursoSelecionado = encontrarCurso(
                cursos,
                idCursoSalvo
            );
        }

        if (!cursoSelecionado && cursos.length > 0) {
            cursoSelecionado = cursos[0];
        }

        if (cursoSelecionado) {
            salvarCursoSelecionado(
                cursoSelecionado.idCurso
            );
        } else {
            limparCursoSelecionado();
        }

        return {
            cursos,
            cursoSelecionado,
            idCurso: cursoSelecionado
                ? Number(cursoSelecionado.idCurso)
                : null
        };
    }

    function atualizarLinksCurso(idCurso) {
        document
            .querySelectorAll("[data-destino-curso]")
            .forEach(link => {
                const destino = link.dataset.destinoCurso;

                link.href = idCurso
                    ? `${destino}?idCurso=${idCurso}`
                    : destino;
            });
    }

    function preencherSeletorCursos(
        seletor,
        cursos,
        idCurso
    ) {
        seletor.innerHTML = "";

        if (cursos.length === 0) {
            const opcao = document.createElement("option");

            opcao.textContent = "Nenhum curso cadastrado";
            seletor.appendChild(opcao);
            seletor.disabled = true;
            return;
        }

        cursos.forEach(curso => {
            const opcao = document.createElement("option");

            opcao.value = curso.idCurso;
            opcao.textContent = curso.nome;
            seletor.appendChild(opcao);
        });

        seletor.value = String(idCurso);
        seletor.disabled = false;
    }

    async function inicializarSeletorGlobal(
        opcoes = {}
    ) {
        const contexto = await resolverCursoAtual();
        const seletor = document.getElementById(
            "seletorCursoGlobal"
        );

        atualizarLinksCurso(contexto.idCurso);

        if (!seletor) {
            return contexto;
        }

        preencherSeletorCursos(
            seletor,
            contexto.cursos,
            contexto.idCurso
        );

        seletor.onchange = async () => {
            const idCurso = salvarCursoSelecionado(
                seletor.value
            );

            const curso = encontrarCurso(
                contexto.cursos,
                idCurso
            );

            atualizarLinksCurso(idCurso);

            if (opcoes.dispararEvento) {
                window.dispatchEvent(
                    new CustomEvent(
                        "cursoAlterado",
                        {
                            detail: {
                                idCurso,
                                curso
                            }
                        }
                    )
                );
            }

            if (opcoes.aoAlterar) {
                await opcoes.aoAlterar(curso);
            }
        };

        return contexto;
    }

    window.CursoContexto = Object.freeze({
        carregarCursos,
        encontrarCurso,
        inicializarSeletorGlobal,
        limparCursoSelecionado,
        obterCursoSelecionadoId,
        obterIdCursoUrl,
        resolverCursoAtual,
        salvarCursoSelecionado
    });
})();
