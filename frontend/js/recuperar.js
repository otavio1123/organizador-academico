const formRecuperar = document.getElementById("formRecuperar");

formRecuperar.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("emailRecuperacao").value.trim();

    try {
        const resposta = await fetch(`${API_URL}/auth/recuperar-senha`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email
            })
        });

        if (!resposta.ok) {
            throw new Error("Erro ao solicitar recuperação.");
        }

Swal.fire({
    icon: "info",
    title: "Solicitação recebida",
    text: "Se houver uma conta cadastrada com esse e-mail, você receberá as instruções para redefinir sua senha. Verifique também a caixa de spam.",
    confirmButtonText: "OK"
});

        formRecuperar.reset();

    } catch (erro) {
        Swal.fire({
            icon: "error",
            title: "Erro ao processar solicitação",
            text: "Não foi possível processar a solicitação. Tente novamente."
        });
    }
});