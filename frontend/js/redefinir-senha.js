const formRedefinirSenha = document.getElementById("formRedefinirSenha");

const parametros = new URLSearchParams(window.location.search);
const token = parametros.get("token");

if (!token) {
    Swal.fire({
        icon: "error",
        title: "Link inválido",
        text: "O link de redefinição de senha é inválido.",
        confirmButtonText: "Voltar para o login"
    }).then(() => {
        window.location.href = "index.html";
    });
}

formRedefinirSenha.addEventListener("submit", async (event) => {
    event.preventDefault();

    const novaSenha = document.getElementById("novaSenha").value;
    const confirmarSenha = document.getElementById("confirmarSenha").value;

    if (novaSenha.length < 8) {
        Swal.fire({
            icon: "warning",
            title: "Senha muito curta",
            text: "A nova senha deve possuir pelo menos 8 caracteres."
        });

        return;
    }

    if (novaSenha !== confirmarSenha) {
        Swal.fire({
            icon: "warning",
            title: "Senhas diferentes",
            text: "A confirmação da senha deve ser igual à nova senha."
        });

        return;
    }

    try {
        const resposta = await fetch(`${API_URL}/auth/redefinir-senha`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                token: token,
                novaSenha: novaSenha
            })
        });

        if (!resposta.ok) {
            const dados = await resposta.json().catch(() => ({}));

            throw new Error(
                dados.mensagem || "O link é inválido ou expirou."
            );
        }

        Swal.fire({
            icon: "success",
            title: "Senha redefinida",
            text: "Sua senha foi alterada com sucesso.",
            confirmButtonText: "Ir para o login"
        }).then(() => {
            window.location.href = "index.html";
        });

    } catch (erro) {
        Swal.fire({
            icon: "error",
            title: "Não foi possível redefinir a senha",
            text: erro.message
        });
    }
});