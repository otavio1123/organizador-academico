const btnTestarApi = document.getElementById("btnTestarApi");
const resultadoApi = document.getElementById("resultadoApi");

btnTestarApi.addEventListener("click", async () => {
  resultadoApi.textContent = "Testando conexão...";

  try {
    const resposta = await fetch(`${API_URL}/teste`);
    const texto = await resposta.text();

    resultadoApi.textContent = texto;
  } catch (erro) {
    resultadoApi.textContent = "Erro ao conectar com a API.";
    console.error("Erro:", erro);
  }
});