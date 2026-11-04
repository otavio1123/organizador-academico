const formRecuperar = document.getElementById("formRecuperar");
const formCodigo = document.getElementById("formCodigo");
const formNovaSenha = document.getElementById("formNovaSenha");

const textoRecuperacao = document.getElementById("textoRecuperacao");

let emailRecuperacao = "";
let codigoRecuperacao = "";

formRecuperar.addEventListener("submit", async (event) => {
    event.preventDefault();

    emailRecuperacao = document
        .getElementById("emailRecuperacao")
        .value
        .trim();

    try {
        const resposta = await fetch(`${API_URL}/auth/recuperar-senha`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: emailRecuperacao
            })
        });

        if (!resposta.ok) {
            throw new Error("Erro ao solicitar recuperação.");
        }

        await Swal.fire({
            icon: "info",
            title: "Código enviado",
            text: "Se houver uma conta cadastrada com esse e-mail, um código de recuperação será enviado. Verifique também a caixa de spam.",
            confirmButtonText: "OK"
        });

        formRecuperar.style.display = "none";
        formCodigo.style.display = "flex";

        textoRecuperacao.textContent =
            "Digite o código de 6 dígitos enviado para o seu e-mail.";

    } catch (erro) {
        Swal.fire({
            icon: "error",
            title: "Erro ao processar solicitação",
            text: "Não foi possível processar a solicitação. Tente novamente."
        });
    }
});


formCodigo.addEventListener("submit", async (event) => {
    event.preventDefault();

    codigoRecuperacao = document
        .getElementById("codigoRecuperacao")
        .value
        .trim();

    if (!/^\d{6}$/.test(codigoRecuperacao)) {
        Swal.fire({
            icon: "warning",
            title: "Código inválido",
            text: "Informe o código de 6 dígitos."
        });

        return;
    }

    try {
        const resposta = await fetch(`${API_URL}/auth/verificar-codigo`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: emailRecuperacao,
                codigo: codigoRecuperacao
            })
        });

        if (!resposta.ok) {
            Swal.fire({
                icon: "error",
                title: "Código inválido",
                text: "O código informado é inválido, expirou ou foi bloqueado."
            });

            return;
        }

        formCodigo.style.display = "none";
        formNovaSenha.style.display = "flex";

        textoRecuperacao.textContent =
            "Código confirmado. Agora escolha uma nova senha.";

    } catch (erro) {
        Swal.fire({
            icon: "error",
            title: "Erro ao verificar código",
            text: "Não foi possível verificar o código. Tente novamente."
        });
    }
});


formNovaSenha.addEventListener("submit", async (event) => {
    event.preventDefault();

    const novaSenha = document.getElementById("novaSenha").value;
    const confirmarNovaSenha =
        document.getElementById("confirmarNovaSenha").value;

    if (novaSenha.length < 8) {
        Swal.fire({
            icon: "warning",
            title: "Senha muito curta",
            text: "A nova senha deve possuir pelo menos 8 caracteres."
        });

        return;
    }

    if (novaSenha !== confirmarNovaSenha) {
        Swal.fire({
            icon: "warning",
            title: "Senhas diferentes",
            text: "A nova senha e a confirmação precisam ser iguais."
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
                email: emailRecuperacao,
                codigo: codigoRecuperacao,
                novaSenha: novaSenha
            })
        });

        if (!resposta.ok) {
            Swal.fire({
                icon: "error",
                title: "Não foi possível redefinir a senha",
                text: "O código pode ter expirado, sido utilizado ou bloqueado."
            });

            return;
        }

        await Swal.fire({
            icon: "success",
            title: "Senha redefinida",
            text: "Sua senha foi alterada com sucesso.",
            confirmButtonText: "Ir para o login"
        });

        window.location.href = "index.html";

    } catch (erro) {
        Swal.fire({
            icon: "error",
            title: "Erro ao redefinir senha",
            text: "Não foi possível redefinir sua senha. Tente novamente."
        });
    }
});

const novaSenha = document.getElementById("novaSenha");
const confirmarNovaSenha = document.getElementById("confirmarNovaSenha");

novaSenha.style.paddingRight = "40px";

const olhoNovaSenha = document.createElement("i");

olhoNovaSenha.className = "fa-solid fa-eye-slash";

olhoNovaSenha.style.position = "absolute";
olhoNovaSenha.style.right = "12px";
olhoNovaSenha.style.cursor = "pointer";
olhoNovaSenha.style.top = "50%";
olhoNovaSenha.style.transform = "translateY(-50%)";

novaSenha.parentElement.style.position = "relative";
novaSenha.parentElement.appendChild(olhoNovaSenha);

olhoNovaSenha.onclick = function () {

    if (novaSenha.type === "password") {

        novaSenha.type = "text";
        olhoNovaSenha.className = "fa-solid fa-eye";

    } else {

        novaSenha.type = "password";
        olhoNovaSenha.className = "fa-solid fa-eye-slash";

    }
};


confirmarNovaSenha.style.paddingRight = "40px";

const olhoConfirmarNovaSenha = document.createElement("i");

olhoConfirmarNovaSenha.className = "fa-solid fa-eye-slash";

olhoConfirmarNovaSenha.style.position = "absolute";
olhoConfirmarNovaSenha.style.right = "12px";
olhoConfirmarNovaSenha.style.cursor = "pointer";
olhoConfirmarNovaSenha.style.top = "50%";
olhoConfirmarNovaSenha.style.transform = "translateY(-50%)";

confirmarNovaSenha.parentElement.style.position = "relative";
confirmarNovaSenha.parentElement.appendChild(olhoConfirmarNovaSenha);

olhoConfirmarNovaSenha.onclick = function () {

    if (confirmarNovaSenha.type === "password") {

        confirmarNovaSenha.type = "text";
        olhoConfirmarNovaSenha.className = "fa-solid fa-eye";

    } else {

        confirmarNovaSenha.type = "password";
        olhoConfirmarNovaSenha.className = "fa-solid fa-eye-slash";

    }
};