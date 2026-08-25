const formCadastro = document.getElementById("formCadastro");
  const senha = document.getElementById("senha");
  const ConfirmarSenha = document.getElementById("ConfirmarSenha");

  senha.style.paddingRight = "40px";

 const olho = document.createElement("i");

olho.className = "fa-solid fa-eye-slash";

olho.style.position = "absolute";
olho.style.right = "12px";
olho.style.cursor = "pointer";
olho.style.top = "50%";
olho.style.transform = "translateY(-50%)";

senha.parentElement.style.position = "relative";
senha.parentElement.appendChild(olho);

olho.onclick = function () {

    if (senha.type === "password") {

        senha.type = "text";
        olho.className = "fa-solid fa-eye";

    } else {

        senha.type = "password";
        olho.className = "fa-solid fa-eye-slash";

    }
};
ConfirmarSenha.style.paddingRight = "40px";

const olhoConfirmar = document.createElement("i");

olhoConfirmar.className = "fa-solid fa-eye-slash";

olhoConfirmar.style.position = "absolute";
olhoConfirmar.style.right = "12px";
olhoConfirmar.style.cursor = "pointer";
olhoConfirmar.style.top = "50%";
olhoConfirmar.style.transform = "translateY(-50%)";

ConfirmarSenha.parentElement.style.position = "relative";
ConfirmarSenha.parentElement.appendChild(olhoConfirmar);

olhoConfirmar.onclick = function () {

    if (ConfirmarSenha.type === "password") {

        ConfirmarSenha.type = "text";
        olhoConfirmar.className = "fa-solid fa-eye";

    } else {

        ConfirmarSenha.type = "password";
        olhoConfirmar.className = "fa-solid fa-eye-slash";

    }
};

formCadastro.addEventListener("submit", async (event) => {
  event.preventDefault();

  const nome = document.getElementById("nome").value;
  const email = document.getElementById("email").value;
  const senha = document.getElementById("senha").value;
  const confirmarSenha = document.getElementById("ConfirmarSenha").value;

  if (senha !== confirmarSenha) {
    Swal.fire({
        icon: "error",
        title: "Senhas diferentes",
        text: "A senha e a confirmação de senha precisam ser iguais."
    });

    return;
}

  const usuario = {
    nome: nome,
    email: email,
    senha: senha
  };

  try {

  const resposta = await fetch(`${API_URL}/usuarios`, {
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
      title: "Cadastro inválido",
      text: dados.mensagem || "Por favor, verifique os campos acima."
    });

    return;
  }

  Swal.fire({
    icon: "success",
    title: "Sucesso!",
    text: "Cadastro realizado com sucesso",
    confirmButtonText: "Continuar"
  }).then(() => {
    formCadastro.reset();
    window.location.href = "index.html";
  });

} catch (erro) {
  Swal.fire({
    icon: "error",
    title: "Erro de conexão",
    text: "Não foi possível conectar ao servidor."
  });
}

});