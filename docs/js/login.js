const formConfirmar = document.getElementById("formConfirmar");
const campoSenha = document.getElementById("senha");

campoSenha.style.paddingRight = "40px";

const olho = document.createElement("i");

olho.className = "fa-solid fa-eye-slash";

olho.style.position = "absolute";
olho.style.right = "12px";
olho.style.cursor = "pointer";
olho.style.top = "50%";
olho.style.transform = "translateY(-50%)";

campoSenha.parentElement.style.position = "relative";
campoSenha.parentElement.appendChild(olho);

olho.onclick = function () {

    if (campoSenha.type === "password") {
        campoSenha.type = "text";
        olho.className = "fa-solid fa-eye";
    } else {
        campoSenha.type = "password";
        olho.className = "fa-solid fa-eye-slash";
    }
};

formConfirmar.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;

    const usuario = {
        email: email,
        senha: senha
    };

    try {

        const resposta = await fetch(`${API_URL}/usuarios/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(usuario)
        });

        const dados = await resposta.json();

        if (!resposta.ok) {

            Swal.fire({
                icon: "error",
                title: "Login inválido",
                text: dados.mensagem || "E-mail ou senha incorretos."
            });

            return;
        }
        const resultadoCodigo = await Swal.fire({
            title: "Verificação em duas etapas",
            text: "Digite o código de 6 dígitos enviado para seu e-mail.",
            input: "text",
            inputPlaceholder: "Código de 6 dígitos",
            inputAttributes: {
                maxlength: "6",
                inputmode: "numeric"
            },
            confirmButtonText: "Verificar"
        });

        if (!resultadoCodigo.isConfirmed) {
            return;
        }

        const codigo = resultadoCodigo.value;

        if (!/^\d{6}$/.test(codigo)) {
            Swal.fire({
                icon: "warning",
                title: "Código inválido",
                text: "Digite os 6 dígitos."
            });
            return;
        }
        const resposta2FA = await fetch(`${API_URL}/auth/verificar-2fa`, {
    method: "POST",
    headers: {
        "Content-Type": "application/json"
    },
    body: JSON.stringify({
        email: email,
        codigo: codigo
    })
});

const dados2FA = await resposta2FA.json();

if (!resposta2FA.ok) {
    Swal.fire({
        icon: "error",
        title: "Código inválido",
        text: dados2FA.mensagem || "O código informado é inválido."
    });
    return;
}

localStorage.setItem(
    "usuarioLogado",
    JSON.stringify(dados2FA.usuario)
);

localStorage.setItem(
    "token",
    dados2FA.token
);

        Swal.fire({
            icon: "success",
            title: "Login realizado!",
            text: "Bem-vindo ao Organizador Acadêmico.",
            confirmButtonText: "Continuar"
        }).then(() => {
            window.location.href = "home.html";
        });

    } catch (erro) {

        console.error("Erro:", erro);

        Swal.fire({
            icon: "error",
            title: "Erro de conexão",
            text: "Não foi possível conectar ao servidor."
        });
    }
});