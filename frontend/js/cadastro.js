const formCadastro = document.getElementById("formCadastro");
const mensagemCadastro = document.getElementById("mensagemCadastro");

  const senha = document.getElementById("senha");

  senha.style.paddingRight = "40px";

  const olho = document.createElement("span");
  olho.innerHTML = "👁";
  olho.style.position = "absolute";
  olho.style.right = "12px";
  olho.style.cursor = "pointer";
  olho.style.top = "50%";
  olho.style.transform = "translateY(-50%)";
  
  senha.parentElement.style.position = "relative";
  senha.parentElement.appendChild(olho);

  olho.onclick = function () {
      senha.type = senha.type === "password" ? "text" : "password";
  };


formCadastro.addEventListener("submit", async (event) => {
  event.preventDefault();

  const nome = document.getElementById("nome").value;
  const email = document.getElementById("email").value;
  const senha = document.getElementById("senha").value;

  const usuario = {
    nome: nome,
    email: email,
    senha: senha
  };

  try {
    mensagemCadastro.textContent = "Cadastrando usuário...";

    const resposta = await fetch(`${API_URL}/usuarios`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(usuario)
    });

    if (!resposta.ok) {
      throw new Error("Erro ao cadastrar usuário.");
    }

    const usuarioCadastrado = await resposta.json();

    mensagemCadastro.textContent = `Usuário cadastrado com sucesso! ID: ${usuarioCadastrado.id}`;
    formCadastro.reset();

  } catch (erro) {
    mensagemCadastro.textContent = "Erro ao cadastrar. Verifique o backend e o banco.";
    console.error("Erro:", erro);
  }


});