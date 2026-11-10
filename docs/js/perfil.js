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

const nomeUsuarioLogado =
    document.getElementById("nomeUsuarioLogado");

const emailUsuarioLogado =
    document.getElementById("emailUsuarioLogado");

const nomePerfil =
    document.getElementById("nomePerfil");

const emailPerfil =
    document.getElementById("emailPerfil");

const btnAlterarFoto =
    document.getElementById("btnAlterarFoto");

const inputFotoPerfil =
    document.getElementById("inputFotoPerfil");

const imagemPerfil =
    document.getElementById("imagemPerfil");

const iconePerfil =
    document.getElementById("iconePerfil");

const imagemUsuarioTopo =
    document.getElementById("imagemUsuarioTopo");

const iconeUsuarioTopo =
    document.getElementById("iconeUsuarioTopo");

if (nomeUsuarioLogado) {
    nomeUsuarioLogado.textContent =
        usuarioLogado.nome;
}

if (emailUsuarioLogado) {
    emailUsuarioLogado.textContent =
        usuarioLogado.email;
}

nomePerfil.value =
    usuarioLogado.nome;

emailPerfil.value =
    usuarioLogado.email;

async function carregarPerfil() {

    try {

        const resposta = await fetch(
            "http://localhost:8080/usuarios/perfil",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        const dados =
            await resposta.json();

        if (!resposta.ok) {
            return;
        }

        if (dados.usuario) {

            usuarioLogado.nome =
                dados.usuario.nome;

            usuarioLogado.email =
                dados.usuario.email;

            usuarioLogado.fotoPerfil =
                dados.usuario.fotoPerfil;

            localStorage.setItem(
                "usuarioLogado",
                JSON.stringify(usuarioLogado)
            );

            nomePerfil.value =
                dados.usuario.nome;

            emailPerfil.value =
                dados.usuario.email;

            if (nomeUsuarioLogado) {
                nomeUsuarioLogado.textContent =
                    dados.usuario.nome;
            }

            if (emailUsuarioLogado) {
                emailUsuarioLogado.textContent =
                    dados.usuario.email;
            }
        }

 if (dados.fotoUrl) {

    imagemPerfil.src =
        dados.fotoUrl;

    imagemPerfil.classList.remove(
        "d-none"
    );

    iconePerfil.classList.add(
        "d-none"
    );

    if (imagemUsuarioTopo) {

        imagemUsuarioTopo.src =
            dados.fotoUrl;

        imagemUsuarioTopo.classList.remove(
            "d-none"
        );
    }

    if (iconeUsuarioTopo) {

        iconeUsuarioTopo.classList.add(
            "d-none"
        );
    }

} else {

    imagemPerfil.src = "";

    imagemPerfil.classList.add(
        "d-none"
    );

    iconePerfil.classList.remove(
        "d-none"
    );

    if (imagemUsuarioTopo) {

        imagemUsuarioTopo.src = "";

        imagemUsuarioTopo.classList.add(
            "d-none"
        );
    }

    if (iconeUsuarioTopo) {

        iconeUsuarioTopo.classList.remove(
            "d-none"
        );
    }
}

    } catch (erro) {

        console.error(
            "Erro ao carregar perfil:",
            erro
        );
    }
}

carregarPerfil();

btnAlterarFoto.addEventListener(
    "click",
    function () {
        inputFotoPerfil.click();
    }
);

inputFotoPerfil.addEventListener(
    "change",
    async function () {

        const arquivo =
            inputFotoPerfil.files[0];

        if (!arquivo) {
            return;
        }

        const tiposPermitidos = [
            "image/jpeg",
            "image/png",
            "image/webp"
        ];

        if (!tiposPermitidos.includes(arquivo.type)) {

            Swal.fire({
                icon: "warning",
                title: "Imagem inválida",
                text: "Use uma imagem JPG, PNG ou WEBP."
            });

            inputFotoPerfil.value = "";

            return;
        }

        if (arquivo.size > 5 * 1024 * 1024) {

            Swal.fire({
                icon: "warning",
                title: "Imagem muito grande",
                text: "A imagem deve ter no máximo 5 MB."
            });

            inputFotoPerfil.value = "";

            return;
        }

        const formData =
            new FormData();

        formData.append(
            "arquivo",
            arquivo
        );

        try {

            const resposta =
                await fetch(
                    "http://localhost:8080/usuarios/foto",
                    {
                        method: "POST",
                        headers: {
                            "Authorization":
                                "Bearer " + token
                        },
                        body: formData
                    }
                );

            const dados =
                await resposta.json();

            if (!resposta.ok) {

                Swal.fire({
                    icon: "error",
                    title: "Erro",
                    text: dados.mensagem ||
                        "Não foi possível atualizar a foto."
                });

                return;
            }

            localStorage.setItem(
                "usuarioLogado",
                JSON.stringify(dados.usuario)
            );

            usuarioLogado.fotoPerfil =
                dados.usuario.fotoPerfil;

            const leitor =
                new FileReader();
leitor.onload =
    function (evento) {

        imagemPerfil.src =
            evento.target.result;

        imagemPerfil.classList.remove(
            "d-none"
        );

        iconePerfil.classList.add(
            "d-none"
        );

        if (imagemUsuarioTopo) {

            imagemUsuarioTopo.src =
                evento.target.result;

            imagemUsuarioTopo.classList.remove(
                "d-none"
            );
        }

        if (iconeUsuarioTopo) {

            iconeUsuarioTopo.classList.add(
                "d-none"
            );
        }
    };

            leitor.readAsDataURL(
                arquivo
            );

            Swal.fire({
                icon: "success",
                title: "Foto atualizada",
                text: dados.mensagem
            });

            inputFotoPerfil.value = "";

        } catch (erro) {

            Swal.fire({
                icon: "error",
                title: "Erro",
                text: "Não foi possível conectar ao servidor."
            });
        }
    }
);

const formPerfil =
    document.getElementById("formPerfil");

formPerfil.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const nome =
            nomePerfil.value.trim();

        if (!nome) {

            Swal.fire({
                icon: "warning",
                title: "Nome inválido",
                text: "Informe seu nome."
            });

            return;
        }

        try {

            const resposta = await fetch(
                "http://localhost:8080/usuarios/perfil",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify({
                        nome: nome
                    })
                }
            );

            const dados =
                await resposta.json();

            if (!resposta.ok) {

                Swal.fire({
                    icon: "error",
                    title: "Erro",
                    text: dados.mensagem ||
                        "Não foi possível atualizar o perfil."
                });

                return;
            }

            localStorage.setItem(
                "usuarioLogado",
                JSON.stringify(dados.usuario)
            );

            usuarioLogado.nome =
                dados.usuario.nome;

            nomePerfil.value =
                dados.usuario.nome;

            if (nomeUsuarioLogado) {
                nomeUsuarioLogado.textContent =
                    dados.usuario.nome;
            }

            Swal.fire({
                icon: "success",
                title: "Perfil atualizado",
                text: dados.mensagem
            });

        } catch (erro) {

            Swal.fire({
                icon: "error",
                title: "Erro",
                text: "Não foi possível conectar ao servidor."
            });
        }
    }
);

const senhaAtual =
    document.getElementById("senhaAtual");

const novaSenha =
    document.getElementById("novaSenha");

const confirmarNovaSenha =
    document.getElementById("confirmarNovaSenha");

function adicionarOlho(campo) {

    const grupo =
        campo.parentElement;

    grupo.style.position =
        "relative";

    campo.style.paddingRight =
        "45px";

    const olho =
        document.createElement("i");

    olho.className =
        "fa-solid fa-eye-slash";

    olho.style.position =
        "absolute";

    olho.style.right =
        "14px";

    olho.style.bottom =
        "13px";

    olho.style.cursor =
        "pointer";

    olho.style.color =
        "#64748b";

    grupo.appendChild(olho);

    olho.addEventListener(
        "click",
        function () {

            if (campo.type === "password") {

                campo.type =
                    "text";

                olho.className =
                    "fa-solid fa-eye";

            } else {

                campo.type =
                    "password";

                olho.className =
                    "fa-solid fa-eye-slash";
            }
        }
    );
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

        if (
            novaSenhaValor !==
            confirmarNovaSenhaValor
        ) {

            Swal.fire({
                icon: "warning",
                title: "Senhas diferentes",
                text: "A nova senha e a confirmação devem ser iguais."
            });

            return;
        }

        if (
            novaSenhaValor.length < 8
        ) {

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

            const dados =
                await resposta.json();

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
    .addEventListener(
        "click",
        async function () {

            try {

                await fetch(
                    "http://localhost:8080/LogAuditoria",
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer " + token
                        },
                        body: JSON.stringify({
                            acao: "Saiu do sistema"
                        })
                    }
                );

            } catch (erro) {

                console.error(
                    "Erro ao registrar logout:",
                    erro
                );
            }

            localStorage.removeItem(
                "usuarioLogado"
            );

            localStorage.removeItem(
                "token"
            );

            window.location.href =
                "index.html";
        }
    );

document.getElementById("btnExcluirConta")
    .addEventListener(
        "click",
        async function () {

            const resultado =
                await Swal.fire({
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

            const senha =
                resultado.value;

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

                const dados =
                    await resposta.json();

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

                localStorage.removeItem(
                    "usuarioLogado"
                );

                localStorage.removeItem(
                    "token"
                );

                window.location.href =
                    "index.html";

            } catch (erro) {

                Swal.fire({
                    icon: "error",
                    title: "Erro",
                    text: "Não foi possível conectar ao servidor."
                });
            }
        }
    );