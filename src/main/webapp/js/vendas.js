/* ============================================================
   VENDAS.JS — listagem e formulário de vendas via API REST
   ============================================================ */

async function inicializarListaVendas() {
  const sessao = Util.protegerRota();
  if (!sessao) return;

  const corpoTabela = document.querySelector('#tabela-vendas tbody');
  const campoBusca = document.querySelector('#busca-venda');
  const filtroVendedor = document.querySelector('#filtro-vendedor');
  const contador = document.querySelector('[data-contador-vendas]');

  if (filtroVendedor) {
    if (sessao.isGerente) {
      try {
        const listaVendedores = await GC.vendedores.listar();
        filtroVendedor.innerHTML = '<option value="">Todos os vendedores</option>' +
          listaVendedores.map((v) => `<option value="${v.id}">${v.nomeCompleto}</option>`).join('');
      } catch (erro) { /* silencioso: filtro é opcional */ }
    } else {
      filtroVendedor.style.display = 'none';
    }
  }

  async function renderizar() {
    try {
      const filtros = { cliente: campoBusca?.value || '' };
      if (sessao.isGerente) {
        if (filtroVendedor?.value) filtros.vendedorId = filtroVendedor.value;
      } else {
        filtros.vendedorId = sessao.id;
      }

      const lista = await GC.vendas.listar(filtros);
      if (contador) contador.textContent = `${lista.length} venda(s)`;

      if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="7"><div class="estado-vazio"><h3>Nenhuma venda encontrada</h3><p>Ajuste os filtros ou registre uma nova venda.</p></div></td></tr>`;
        return;
      }

      corpoTabela.innerHTML = lista.map((v) => `
        <tr>
          <td>${v.clienteNome || '—'}</td>
          <td>${v.produtoNome || 'Produto removido'}</td>
          <td class="numerico">${v.quantidade ?? '—'}</td>
          <td class="numerico">${v.percentualDesconto ?? 0}%</td>
          <td class="numerico">${Util.formatarMoeda(v.valorTotal || 0)}</td>
          <td>${v.vendedorNome || '—'}</td>
          <td>
            <div class="col-acoes">
              <button class="btn btn--perigo btn--pequeno" data-excluir="${v.id}" type="button">Excluir</button>
            </div>
          </td>
        </tr>`).join('');

      corpoTabela.querySelectorAll('[data-excluir]').forEach((botao) => {
        botao.addEventListener('click', async () => {
          if (!confirm('Deseja realmente excluir esta venda? O estoque do produto será restaurado.')) return;
          try {
            await GC.vendas.remover(botao.dataset.excluir);
            Util.exibirToast('Venda excluída com sucesso.', 'sucesso');
            renderizar();
          } catch (erro) {
            Util.exibirToast('Erro: ' + erro.message, 'erro');
          }
        });
      });
    } catch (erro) {
      Util.exibirToast('Erro ao carregar vendas: ' + erro.message, 'erro');
    }
  }

  campoBusca?.addEventListener('input', renderizar);
  filtroVendedor?.addEventListener('change', renderizar);
  renderizar();
}

async function inicializarFormularioVenda() {
  const sessao = Util.protegerRota();
  if (!sessao) return;

  const form = document.querySelector('#form-venda');
  const campoCliente = document.querySelector('#clienteId');
  const campoProduto = document.querySelector('#produtoId');
  const campoQuantidade = document.querySelector('#quantidade');
  const campoPagamento = document.querySelector('#formaPagamento');

  const saidaPreco = document.querySelector('[data-saida="preco-unitario"]');
  const saidaSubtotal = document.querySelector('[data-saida="subtotal"]');
  const saidaDesconto = document.querySelector('[data-saida="desconto"]');
  const saidaTotal = document.querySelector('[data-saida="total"]');
  const saidaEstoque = document.querySelector('[data-saida="estoque-disponivel"]');
  const faixaDesconto = document.querySelector('[data-faixa-desconto]');

  let produtos = [];
  try {
    const [listaClientes, listaProdutos] = await Promise.all([GC.clientes.listar(), GC.produtos.listar()]);
    produtos = listaProdutos;

    campoCliente.innerHTML = '<option value="">Selecione um cliente…</option>' +
      listaClientes.map((c) => `<option value="${c.id}">${c.nomeCompleto}</option>`).join('');

    if (listaClientes.length === 0) {
      campoCliente.innerHTML = '<option value="">Nenhum cliente cadastrado</option>';
      Util.exibirToast('Cadastre um cliente antes de registrar uma venda.', 'info');
    }

    campoProduto.innerHTML = '<option value="">Selecione um produto…</option>' +
      produtos.map((p) => `<option value="${p.id}">${p.nomeProd} — ${Util.formatarMoeda(p.preco)}</option>`).join('');
  } catch (erro) {
    Util.exibirToast('Erro ao carregar dados do formulário: ' + erro.message, 'erro');
  }

  function atualizarResumo() {
    const produto = produtos.find((p) => p.id === Number(campoProduto.value));
    const quantidade = Number(campoQuantidade.value) || 0;

    if (!produto) {
      saidaPreco.textContent = '—';
      saidaEstoque.textContent = '—';
      saidaSubtotal.textContent = Util.formatarMoeda(0);
      saidaDesconto.textContent = Util.formatarMoeda(0);
      saidaTotal.textContent = Util.formatarMoeda(0);
      return;
    }

    saidaPreco.textContent = Util.formatarMoeda(produto.preco);
    saidaEstoque.textContent = `${produto.quantidade} un. em estoque`;

    const { subtotal, percentualDesconto, valorDesconto, total } = GC.calcularVenda(produto.preco, quantidade);
    saidaSubtotal.textContent = Util.formatarMoeda(subtotal);
    saidaDesconto.textContent = `− ${Util.formatarMoeda(valorDesconto)} (${percentualDesconto}%)`;
    saidaTotal.textContent = Util.formatarMoeda(total);

    if (faixaDesconto) {
      faixaDesconto.textContent = percentualDesconto === 0
        ? 'Sem desconto (menos de 5 unidades)'
        : percentualDesconto === 5 ? 'Desconto de 5% (5 a 9 unidades)' : 'Desconto de 10% (10 unidades ou mais)';
    }

    if (quantidade > produto.quantidade) {
      Util.marcarInvalido(campoQuantidade, `Estoque insuficiente. Disponível: ${produto.quantidade} un.`);
    } else {
      Util.limparInvalido(campoQuantidade);
    }
  }

  campoProduto.addEventListener('change', atualizarResumo);
  campoQuantidade.addEventListener('input', atualizarResumo);
  atualizarResumo();

  form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    let valido = true;

    if (!campoCliente.value) { Util.marcarInvalido(campoCliente, 'Selecione um cliente.'); valido = false; }
    else Util.limparInvalido(campoCliente);

    if (!campoProduto.value) { Util.marcarInvalido(campoProduto, 'Selecione um produto.'); valido = false; }
    else Util.limparInvalido(campoProduto);

    const produto = produtos.find((p) => p.id === Number(campoProduto.value));
    const quantidade = Number(campoQuantidade.value);

    if (!quantidade || quantidade <= 0) { Util.marcarInvalido(campoQuantidade, 'Informe uma quantidade válida.'); valido = false; }
    else if (produto && quantidade > produto.quantidade) { Util.marcarInvalido(campoQuantidade, `Estoque insuficiente. Disponível: ${produto.quantidade} un.`); valido = false; }
    else Util.limparInvalido(campoQuantidade);

    if (!valido || !produto) return;

    try {
      await GC.vendas.registrar({
        clienteId: Number(campoCliente.value),
        produtoId: Number(campoProduto.value),
        vendedorId: sessao.id,
        quantidade,
        formaPagamento: campoPagamento ? campoPagamento.value : 'Dinheiro',
      });
      Util.exibirToast('Venda registrada com sucesso.', 'sucesso');
      window.location.href = 'vendas.html';
    } catch (erro) {
      Util.exibirToast('Erro ao registrar venda: ' + erro.message, 'erro');
    }
  });
}
