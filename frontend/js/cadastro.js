const formCadastro =
    document.getElementById("formCadastro");

const senha =
    document.getElementById("senha");

const ConfirmarSenha =
    document.getElementById("ConfirmarSenha");

senha.style.paddingRight = "45px";

const olho =
    document.createElement("i");

olho.className =
    "fa-solid fa-eye-slash";

olho.style.position =
    "absolute";

olho.style.right =
    "16px";

olho.style.cursor =
    "pointer";

olho.style.top =
    "50%";

olho.style.transform =
    "translateY(-50%)";

olho.style.color =
    "#64748b";

senha.parentElement.style.position =
    "relative";

senha.parentElement.appendChild(
    olho
);

olho.onclick = function () {

    if (senha.type === "password") {

        senha.type = "text";

        olho.className =
            "fa-solid fa-eye";

    } else {

        senha.type = "password";

        olho.className =
            "fa-solid fa-eye-slash";
    }
};

ConfirmarSenha.style.paddingRight =
    "45px";

const olhoConfirmar =
    document.createElement("i");

olhoConfirmar.className =
    "fa-solid fa-eye-slash";

olhoConfirmar.style.position =
    "absolute";

olhoConfirmar.style.right =
    "16px";

olhoConfirmar.style.cursor =
    "pointer";

olhoConfirmar.style.top =
    "50%";

olhoConfirmar.style.transform =
    "translateY(-50%)";

olhoConfirmar.style.color =
    "#64748b";

ConfirmarSenha.parentElement.style.position =
    "relative";

ConfirmarSenha.parentElement.appendChild(
    olhoConfirmar
);

olhoConfirmar.onclick = function () {

    if (ConfirmarSenha.type === "password") {

        ConfirmarSenha.type =
            "text";

        olhoConfirmar.className =
            "fa-solid fa-eye";

    } else {

        ConfirmarSenha.type =
            "password";

        olhoConfirmar.className =
            "fa-solid fa-eye-slash";
    }
};

formCadastro.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const nome =
            document
                .getElementById("nome")
                .value
                .trim();

        const email =
            document
                .getElementById("email")
                .value
                .trim();

        const senhaValor =
            senha.value;

        const confirmarSenha =
            ConfirmarSenha.value;

        const aceitouTermos =
            document
                .getElementById("confirmacao")
                .checked
                ? "S"
                : "N";

        if (senhaValor.length < 8) {

            Swal.fire({
                icon: "warning",
                title: "Senha muito curta",
                text: "A senha deve ter pelo menos 8 caracteres."
            });

            return;
        }

        if (
            senhaValor !==
            confirmarSenha
        ) {

            Swal.fire({
                icon: "error",
                title: "Senhas diferentes",
                text: "A senha e a confirmação de senha precisam ser iguais."
            });

            return;
        }

        if (aceitouTermos !== "S") {

            Swal.fire({
                icon: "warning",
                title: "Aceite necessário",
                text: "É necessário aceitar os Termos de Uso e a Política de Privacidade."
            });

            return;
        }

        const usuario = {
            nome: nome,
            email: email,
            senha: senhaValor,
            aceitouTermos: aceitouTermos
        };

        try {

            const resposta =
                await fetch(
                    `${API_URL}/usuarios`,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type":
                                "application/json"
                        },
                        body:
                            JSON.stringify(
                                usuario
                            )
                    }
                );

            const dados =
                await resposta.json();

            if (!resposta.ok) {

                Swal.fire({
                    icon: "error",
                    title: "Cadastro inválido",
                    text:
                        dados.mensagem ||
                        "Por favor, verifique os campos acima."
                });

                return;
            }

            await Swal.fire({
                icon: "success",
                title: "Sucesso!",
                text: "Cadastro realizado com sucesso.",
                confirmButtonText: "Continuar"
            });

            formCadastro.reset();

            window.location.href =
                "index.html";

        } catch (erro) {

            Swal.fire({
                icon: "error",
                title: "Erro de conexão",
                text: "Não foi possível conectar ao servidor."
            });
        }
    }
);