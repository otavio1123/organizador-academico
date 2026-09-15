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

        localStorage.setItem("usuarioLogado", JSON.stringify(dados));
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