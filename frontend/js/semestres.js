const filtroStatusSemestre =
    document.getElementById("filtroStatusSemestre");

const botaoCriarSemestres =
    document.getElementById("botao-criarSemestres");

const modalCriarSemestre =
    document.getElementById("modalCriarSemestre");

const fecharModal =
    document.getElementById("fecharModal");

const salvarSemestre =
    document.getElementById("salvarSemestre");

const modalStatus =
    document.getElementById("modalStatus");

const fecharModalStatus =
    document.getElementById("fecharModalStatus");

const salvarStatusSemestre =
    document.getElementById("salvarStatusSemestre");

const token =
    localStorage.getItem("token");

if (!token) {
    localStorage.removeItem("usuarioLogado");
    window.location.href = "index.html";
}

let idSemestreSelecionado = null;


filtroStatusSemestre.addEventListener("change", () => {
    carregarSemestres();
});


botaoCriarSemestres.addEventListener("click", () => {
    modalCriarSemestre.style.display = "flex";
});


fecharModal.addEventListener("click", () => {
    modalCriarSemestre.style.display = "none";
});


salvarSemestre.addEventListener("click", async () => {

    const nome =
        document.getElementById("nomeSemestre").value;

    const ano =
        Number(document.getElementById("anoSemestre").value);

    const dataInicio =
        document.getElementById("inicioSemestre").value;

    const dataFim =
        document.getElementById("fimSemestre").value;

    const ativo =
        document.getElementById("ativoSemestre").value;


    if (!nome || !ano || !dataInicio || !dataFim) {

        alert("Preencha todos os campos.");
        return;
    }


    if (dataFim <= dataInicio) {

        alert(
            "A data de fim deve ser maior que a data de início."
        );

        return;
    }


    const semestre = {
        nome: nome,
        ano: ano,
        dataInicio: dataInicio,
        dataFim: dataFim,
        ativo: ativo
    };


    try {

        const resposta = await fetch(
            "http://localhost:8080/Semestres",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(semestre)
            }
        );


        if (!resposta.ok) {

            const mensagem =
                await resposta.text();

            alert(mensagem);
            return;
        }


        const semestreSalvo =
            await resposta.json();

        console.log(
            "Semestre salvo:",
            semestreSalvo
        );

        alert(
            "Semestre criado com sucesso!"
        );


        modalCriarSemestre.style.display =
            "none";


        document.getElementById(
            "nomeSemestre"
        ).value = "";

        document.getElementById(
            "anoSemestre"
        ).value = "";

        document.getElementById(
            "inicioSemestre"
        ).value = "";

        document.getElementById(
            "fimSemestre"
        ).value = "";


        carregarSemestres();

    } catch (erro) {

        console.error(erro);

        alert(
            "Não foi possível criar o semestre."
        );
    }
});


async function carregarSemestres() {

    try {

        const resposta = await fetch(
            "http://localhost:8080/Semestres",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );


        if (!resposta.ok) {

            const mensagem =
                await resposta.text();

            alert(mensagem);
            return;
        }


        const semestres =
            await resposta.json();


        console.log(
            "Semestres recebidos:",
            semestres
        );


        const listaSemestres =
            document.getElementById(
                "listaSemestres"
            );

        listaSemestres.innerHTML = "";


        const statusEscolhido =
            filtroStatusSemestre.value;


        const semestresFiltrados =
            semestres.filter(semestre => {

                return semestre.ativo ===
                    statusEscolhido;
            });


        semestresFiltrados.forEach(
            semestre => {

                const modelo =
                    document.getElementById(
                        "modeloSemestre"
                    );

                const card =
                    modelo.content.cloneNode(true);


                card.querySelector(
                    ".nome-semestre"
                ).textContent =
                    semestre.nome;


                card.querySelector(
                    ".ano-semestre"
                ).textContent =
                    semestre.ano;


                card.querySelector(
                    ".inicio-semestre"
                ).textContent =
                    semestre.dataInicio;


                card.querySelector(
                    ".fim-semestre"
                ).textContent =
                    semestre.dataFim;


                const botaoExcluir =
                    card.querySelector(
                        ".botao-excluir-semestre"
                    );


                const botaoOpcoes =
                    card.querySelector(
                        ".botaoOpcoesSemestre"
                    );


                botaoOpcoes.addEventListener(
                    "click",
                    () => {

                        idSemestreSelecionado =
                            semestre.idSemestre;


                        document.getElementById(
                            "nomeSemestreEditar"
                        ).value =
                            semestre.nome;


                        document.getElementById(
                            "anoSemestreEditar"
                        ).value =
                            semestre.ano;


                        document.getElementById(
                            "statusSemestre"
                        ).value =
                            semestre.ativo;


                        document.getElementById(
                            "inicioSemestreEditar"
                        ).value =
                            semestre.dataInicio;


                        document.getElementById(
                            "fimSemestreEditar"
                        ).value =
                            semestre.dataFim;


                        modalStatus.style.display =
                            "flex";
                    }
                );


                botaoExcluir.addEventListener(
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
                                    `http://localhost:8080/Semestres/${semestre.idSemestre}`,
                                    {
                                        method: "DELETE",
                                        headers: {
                                            "Authorization":
                                                "Bearer " + token
                                        }
                                    }
                                );


                            if (!resposta.ok) {

                                const mensagem =
                                    await resposta.text();

                                alert(mensagem);
                                return;
                            }


                            alert(
                                "Semestre excluído com sucesso!"
                            );


                            carregarSemestres();

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


                listaSemestres.appendChild(
                    card
                );
            }
        );

    } catch (erro) {

        console.error(
            "Erro:",
            erro
        );
    }
}


fecharModalStatus.addEventListener(
    "click",
    () => {

        modalStatus.style.display =
            "none";
    }
);


salvarStatusSemestre.addEventListener(
    "click",
    async () => {

        const nome =
            document.getElementById(
                "nomeSemestreEditar"
            ).value;


        const ano =
            Number(
                document.getElementById(
                    "anoSemestreEditar"
                ).value
            );


        const status =
            document.getElementById(
                "statusSemestre"
            ).value;


        const dataInicio =
            document.getElementById(
                "inicioSemestreEditar"
            ).value;


        const dataFim =
            document.getElementById(
                "fimSemestreEditar"
            ).value;


        if (dataFim <= dataInicio) {

            alert(
                "A data de fim deve ser maior que a data de início."
            );

            return;
        }


        if (!idSemestreSelecionado) {
            return;
        }


        const semestre = {
            nome: nome,
            ano: ano,
            ativo: status,
            dataInicio: dataInicio,
            dataFim: dataFim
        };


        try {

            const resposta = await fetch(
                `http://localhost:8080/Semestres/${idSemestreSelecionado}`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token
                    },

                    body:
                        JSON.stringify(semestre)
                }
            );


            if (!resposta.ok) {

                const mensagem =
                    await resposta.text();

                alert(mensagem);
                return;
            }


            const semestreAtualizado =
                await resposta.json();


            console.log(
                "Semestre atualizado:",
                semestreAtualizado
            );


            alert(
                "Semestre alterado com sucesso!"
            );


            modalStatus.style.display =
                "none";


            carregarSemestres();

        } catch (erro) {

            console.error(
                "Erro:",
                erro
            );

            alert(
                "Não foi possível alterar o semestre."
            );
        }
    }
);


carregarSemestres();