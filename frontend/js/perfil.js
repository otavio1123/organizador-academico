const usuarioLogado = JSON.parse(
    localStorage.getItem("usuarioLogado")
);

const token =
    localStorage.getItem("token");

if (!usuarioLogado || !token) {
    localStorage.removeItem("usuarioLogado");
    localStorage.removeItem("token");
    window.location.href = "index.html";
}

document.getElementById("nomeUsuario").textContent =
    usuarioLogado.nome;

document.getElementById("emailUsuario").textContent =
    usuarioLogado.email;


const senhaAtual =
    document.getElementById("senhaAtual");

const novaSenha =
    document.getElementById("novaSenha");

const confirmarNovaSenha =
    document.getElementById("confirmarNovaSenha");


function adicionarOlho(campo) {

    campo.style.paddingRight = "40px";

    const olho = document.createElement("i");

    olho.className = "fa-solid fa-eye-slash";

    olho.style.position = "absolute";
    olho.style.right = "12px";
    olho.style.cursor = "pointer";
    olho.style.top = "50%";
    olho.style.transform = "translateY(-50%)";

    campo.parentElement.style.position = "relative";
    campo.parentElement.appendChild(olho);

    olho.onclick = function () {

        if (campo.type === "password") {

            campo.type = "text";
            olho.className = "fa-solid fa-eye";

        } else {

            campo.type = "password";
            olho.className = "fa-solid fa-eye-slash";
        }
    };
}


adicionarOlho(senhaAtual);
adicionarOlho(novaSenha);
adicionarOlho(confirmarNovaSenha);


const formAlterarSenha =
    document.getElementById("formAlterarSenha");

formAlterarSenha.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const senhaAtualValor =
            senhaAtual.value;

        const novaSenhaValor =
            novaSenha.value;

        const confirmarNovaSenhaValor =
            confirmarNovaSenha.value;

        if (novaSenhaValor !== confirmarNovaSenhaValor) {

            Swal.fire({
                icon: "warning",
                title: "Senhas diferentes",
                text: "A nova senha e a confirmação devem ser iguais."
            });

            return;
        }

        if (novaSenhaValor.length < 8) {

            Swal.fire({
                icon: "warning",
                title: "Senha inválida",
                text: "A nova senha deve ter pelo menos 8 caracteres."
            });

            return;
        }

        try {

            const resposta = await fetch(
                "http://localhost:8080/usuarios/senha",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify({
                        senhaAtual: senhaAtualValor,
                        novaSenha: novaSenhaValor
                    })
                }
            );

            const dados = await resposta.json();

            if (!resposta.ok) {

                Swal.fire({
                    icon: "error",
                    title: "Erro",
                    text: dados.mensagem
                });

                return;
            }

            Swal.fire({
                icon: "success",
                title: "Senha alterada",
                text: dados.mensagem
            });

            formAlterarSenha.reset();

        } catch (erro) {

            Swal.fire({
                icon: "error",
                title: "Erro",
                text: "Não foi possível conectar ao servidor."
            });
        }
    }
);


document.getElementById("btnSair")
    .addEventListener("click", function () {

        localStorage.removeItem("usuarioLogado");
        localStorage.removeItem("token");

        window.location.href = "index.html";
    });


document.getElementById("btnExcluirConta")
    .addEventListener(
        "click",
        async function () {

            const resultado = await Swal.fire({
                title: "Excluir conta",
                text: "Digite sua senha para confirmar a exclusão.",
                input: "password",
                inputPlaceholder: "Senha atual",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "Excluir conta",
                cancelButtonText: "Cancelar"
            });

            if (!resultado.isConfirmed) {
                return;
            }

            const senha = resultado.value;

            if (!senha) {

                Swal.fire({
                    icon: "warning",
                    title: "Informe sua senha"
                });

                return;
            }

            try {

                const resposta = await fetch(
                    "http://localhost:8080/usuarios",
                    {
                        method: "DELETE",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer " + token
                        },
                        body: JSON.stringify({
                            senha: senha
                        })
                    }
                );

                const dados = await resposta.json();

                if (!resposta.ok) {

                    Swal.fire({
                        icon: "error",
                        title: "Erro",
                        text: dados.mensagem
                    });

                    return;
                }

                await Swal.fire({
                    icon: "success",
                    title: "Conta excluída",
                    text: dados.mensagem
                });

                localStorage.removeItem("usuarioLogado");
                localStorage.removeItem("token");

                window.location.href = "index.html";

            } catch (erro) {

                Swal.fire({
                    icon: "error",
                    title: "Erro",
                    text: "Não foi possível conectar ao servidor."
                });
            }
        }
    );