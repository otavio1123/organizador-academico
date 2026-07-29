const formCadastro = document.getElementById("formCadastro");
const mensagemCadastro = document.getElementById("mensagemCadastro");

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