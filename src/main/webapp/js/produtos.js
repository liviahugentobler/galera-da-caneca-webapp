/* ============================================================
   PRODUTOS.JS — listagem e formulário de produtos via API REST
   ============================================================ */

async function inicializarListaProdutos() {
  const sessao = Util.protegerRota();
  if (!sessao) return;

  const corpoTabela = document.querySelector('#tabela-produtos tbody');
  const campoBusca = document.querySelector('#busca-produto');
  const contador = document.querySelector('[data-contador-produtos]');

  async function renderizar() {
    try {
      const termoBusca = campoBusca ? campoBusca.value : '';
      const lista = await GC.produtos.listar(termoBusca || '');
      if (contador) contador.textContent = `${lista.length} produto(s)`;

      if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="5"><div class="estado-vazio"><h3>Nenhum produto encontrado</h3><p>Ajuste a busca ou cadastre um novo produto.</p></div></td></tr>`;
        return;
      }

      corpoTabela.innerHTML = lista.map((p) => `
        <tr>
          <td>${p.id}</td>
          <td>${p.nomeProd}</td>
          <td class="numerico">${Util.formatarMoeda(p.preco)}</td>
          <td class="numerico">${p.quantidade}${p.quantidade <= 10 ? ' <span class="selo selo--baixo-estoque">baixo</span>' : ''}</td>
          <td>
            <div class="col-acoes">
              <a class="btn btn--secundario btn--pequeno somente-gerente" href="produto-form.html?id=${p.id}">Editar</a>
              <button class="btn btn--perigo btn--pequeno somente-gerente" data-excluir="${p.id}" type="button">Excluir</button>
            </div>
          </td>
        </tr>`).join('');

      if (!sessao.isGerente) corpoTabela.querySelectorAll('.somente-gerente').forEach((el) => el.remove());

      corpoTabela.querySelectorAll('[data-excluir]').forEach((botao) => {
        botao.addEventListener('click', async () => {
          if (!confirm('Deseja realmente excluir este produto?')) return;
          try {
            await GC.produtos.remover(botao.dataset.excluir);
            Util.exibirToast('Produto excluído com sucesso.', 'sucesso');
            renderizar();
          } catch (erro) {
            Util.exibirToast('Erro: ' + erro.message, 'erro');
          }
        });
      });
    } catch (erro) {
      Util.exibirToast('Erro ao carregar produtos: ' + erro.message, 'erro');
    }
  }

  if (campoBusca) campoBusca.addEventListener('input', renderizar);
  renderizar();
}

async function inicializarFormularioProduto() {
  const sessao = Util.protegerRota({ exigirGerente: true });
  if (!sessao) return;

  const idParam = Util.parametroURL('id');
  const form = document.querySelector('#form-produto');
  const titulo = document.querySelector('[data-titulo-form]');
  const campoNome = document.querySelector('#nome');
  const campoPreco = document.querySelector('#preco');
  const campoEstoque = document.querySelector('#estoque');
  const campoId = document.querySelector('#produtoId');

  if (idParam) {
    try {
      const produto = await GC.produtos.buscarPorId(idParam);
      titulo.textContent = 'Editar Produto';
      campoId.value = produto.id;
      campoNome.value = produto.nomeProd;
      campoPreco.value = produto.preco;
      campoEstoque.value = produto.quantidade;
    } catch (erro) {
      Util.exibirToast('Erro ao carregar produto: ' + erro.message, 'erro');
    }
  }

  form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    let valido = true;

    if (campoNome.value.trim().length < 2) { Util.marcarInvalido(campoNome, 'Informe o nome do produto.'); valido = false; }
    else Util.limparInvalido(campoNome);

    if (!campoPreco.value || Number(campoPreco.value) <= 0) { Util.marcarInvalido(campoPreco, 'Informe um preço válido maior que zero.'); valido = false; }
    else Util.limparInvalido(campoPreco);

    if (campoEstoque.value === '' || Number(campoEstoque.value) < 0) { Util.marcarInvalido(campoEstoque, 'Informe a quantidade em estoque (0 ou mais).'); valido = false; }
    else Util.limparInvalido(campoEstoque);

    if (!valido) return;

    try {
      await GC.produtos.salvar({
        id: campoId.value ? Number(campoId.value) : null,
        nomeProd: campoNome.value.trim(),
        preco: Number(campoPreco.value),
        quantidade: Number(campoEstoque.value),
      });
      Util.exibirToast('Produto salvo com sucesso.', 'sucesso');
      window.location.href = 'produtos.html';
    } catch (erro) {
      Util.exibirToast('Erro ao salvar produto: ' + erro.message, 'erro');
    }
  });
}