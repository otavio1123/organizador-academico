const formConfirmar = document.getElementById("formConfirmar");

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