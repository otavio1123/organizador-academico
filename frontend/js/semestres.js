const filtroStatusSemestre = document.getElementById("filtroStatusSemestre");
const botaoCriarSemestres = document.getElementById("botao-criarSemestres");
const modalCriarSemestre = document.getElementById("modalCriarSemestre");
const salvarSemestre = document.getElementById("salvarSemestre");
const tituloModalSemestre = document.getElementById("tituloModalSemestre");

let idSemestreSelecionado = null;

filtroStatusSemestre.addEventListener("change", () => {
    carregarSemestres();

});

botaoCriarSemestres.addEventListener("click", () => {
    idSemestreSelecionado = null;
    tituloModalSemestre.textContent = "Criar novo semestre";
});

salvarSemestre.addEventListener("click", async () => {

    const nome = document.getElementById("nomeSemestre").value;
    const ano = Number(document.getElementById("anoSemestre").value);
    const dataInicio = document.getElementById("inicioSemestre").value;
    const dataFim = document.getElementById("fimSemestre").value;
    const ativo = document.getElementById("ativoSemestre").value;

    if (!nome || !ano || !dataInicio || !dataFim) {
        alert("Preencha todos os campos.");
        return;
    }

    if (dataFim < dataInicio) {
        alert("A data de fim deve ser maior que a data de início.");
        return;
    }

    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));

    const semestre = {
        nome: nome,
        ano: ano,
        dataInicio: dataInicio,
        dataFim: dataFim,
        ativo: ativo,
        idusuario: usuarioLogado.id
    };

    try {

        let resposta;

        if (idSemestreSelecionado === null) {

            resposta = await fetch("http://localhost:8080/Semestres", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(semestre)
            });

        } else {

            resposta = await fetch(`http://localhost:8080/Semestres/${idSemestreSelecionado}`,{
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(semestre)
                }
            );

        }

        if (!resposta.ok) {
            const mensagem = await resposta.text();
            alert(mensagem);
            return;
        }

        const semestreSalvo = await resposta.json();
        
        if (idSemestreSelecionado === null) {
            await fetch("http://localhost:8080/LogAuditoria", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    idUsuario: usuarioLogado.id,
                    nomeUsuario: usuarioLogado.nome,
                    acao: "O " + usuarioLogado.nome + " Criou o semestre: " + semestreSalvo.nome,

                })
            });
        }else{
             await fetch("http://localhost:8080/LogAuditoria", {
                   method: "POST",
                   headers: {
                       "Content-Type": "application/json"
                   },
                   body: JSON.stringify({
                       idUsuario: usuarioLogado.id,
                       nomeUsuario: usuarioLogado.nome,
                       acao: "O " + usuarioLogado.nome + " Alterou o semestre: " + semestreSalvo.nome
                   })
               });
        }    

        console.log("Semestre salvo:", semestreSalvo);

        if (idSemestreSelecionado === null) {
            alert("Semestre criado com sucesso!");
        } else {
            alert("Semestre alterado com sucesso!");
        }

        const modal = bootstrap.Modal.getInstance(modalCriarSemestre);
        modal.hide();

        document.getElementById("nomeSemestre").value = "";
        document.getElementById("anoSemestre").value = "";
        document.getElementById("inicioSemestre").value = "";
        document.getElementById("fimSemestre").value = "";

        idSemestreSelecionado = null;

        tituloModalSemestre.textContent = "Criar novo semestre";

        carregarSemestres();

    } catch (erro) {

        console.error(erro);
        alert("Não foi possível salvar o semestre.");

    }

});

async function carregarSemestres() {
    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));


    if (!usuarioLogado) {
        console.error("Nenhum usuário está logado.");
        return;
    }

    try {
        const resposta = await fetch(
            `http://localhost:8080/Semestres?idusuario=${usuarioLogado.id}`
        );

        if (!resposta.ok) {
             const mensagem = await resposta.text();
            alert(mensagem);
            return;
        }

        const semestres = await resposta.json();
        console.log("Semestres recebidos:", semestres);

        const listaSemestres = document.getElementById("listaSemestres");
        listaSemestres.innerHTML = "";

        const statusEscolhido = filtroStatusSemestre.value;

        const semestresFiltrados = semestres.filter(semestre => {
            return semestre.ativo === statusEscolhido;
        });

        semestresFiltrados.forEach(semestre => {
            const modelo = document.getElementById("modeloSemestre");
            const card = modelo.content.cloneNode(true);

            card.querySelector(".nome-semestre").textContent = semestre.nome;
            card.querySelector(".ano-semestre").textContent = semestre.ano;
            card.querySelector(".inicio-semestre").textContent = semestre.dataInicio;
            card.querySelector(".fim-semestre").textContent = semestre.dataFim;

            const botaoExcluir = card.querySelector(".botao-excluir-semestre");
            const botaoOpcoes = card.querySelector(".botaoOpcoesSemestre");

            botaoOpcoes.addEventListener("click", () => {
                idSemestreSelecionado = semestre.idSemestre;

                document.getElementById("nomeSemestre").value = semestre.nome;
                document.getElementById("anoSemestre").value = semestre.ano;
                document.getElementById("ativoSemestre").value = semestre.ativo;
                document.getElementById("inicioSemestre").value = semestre.dataInicio;
                document.getElementById("fimSemestre").value = semestre.dataFim;

                tituloModalSemestre.textContent = "Editar semestre";

                const modal = new bootstrap.Modal(modalCriarSemestre);
                modal.show();
            });

            botaoExcluir.addEventListener("click", async () => {
                const confirmar = confirm(
                    `Deseja realmente excluir o semestre "${semestre.nome}"?`
                );

                if (!confirmar) {
                    return;
                }

                try {
                    const resposta = await fetch(
                        `http://localhost:8080/Semestres/${semestre.idSemestre}`,
                        {
                            method: "DELETE"
                        }
                    );

                   if (!resposta.ok) {
                        const mensagem = await resposta.text();
                        alert(mensagem);
                        return;
                    }
                     await fetch("http://localhost:8080/LogAuditoria", {
                        method: "POST",
                        headers: {"Content-Type": "application/json"},
                        body: JSON.stringify({
                            idUsuario: usuarioLogado.id,
                            nomeUsuario: usuarioLogado.nome,
                            acao: "O " + usuarioLogado.nome + " Excluiu o semestre: " + semestre.nome
                        })
                    });

                    alert("Semestre excluído com sucesso!");
                    carregarSemestres();
                } catch (erro) {
                    console.error("Erro:", erro);
                    alert("Não foi possível excluir o semestre.");
                }
            });

            listaSemestres.appendChild(card);
        });
    } catch (erro) {
        console.error("Erro:", erro);
    }
}

carregarSemestres();