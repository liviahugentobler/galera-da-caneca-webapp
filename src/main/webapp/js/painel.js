/* ============================================================
   PAINEL.JS — métricas do dashboard (via GET /api/dashboard)
   ============================================================ */
document.addEventListener('DOMContentLoaded', async () => {
  const sessao = Util.protegerRota();
  if (!sessao) return;
  try {
    const dados = await GC.dashboard.metricas(sessao.id, sessao.isGerente);
    document.querySelector('[data-metrica="total-vendido"]').textContent = Util.formatarMoeda(dados.totalVendido);
    document.querySelector('[data-metrica="qtd-vendas"]').textContent = dados.qtdVendas;
    document.querySelector('[data-metrica="unidades"]').textContent = dados.unidades;
    const cartaoEstoque = document.querySelector('[data-metrica="baixo-estoque"]');
    if (cartaoEstoque) cartaoEstoque.textContent = dados.produtosBaixoEstoque;
    const cartaoVendedores = document.querySelector('[data-metrica="qtd-vendedores"]');
    if (cartaoVendedores) cartaoVendedores.textContent = dados.qtdVendedores;
    const corpoTabela = document.querySelector('#tabela-ultimas-vendas tbody');
    if (corpoTabela) {
      if (!dados.ultimasVendas || dados.ultimasVendas.length === 0) {
        corpoTabela.innerHTML = '<tr><td colspan="5">Nenhuma venda registrada ainda.</td></tr>';
      } else {
        corpoTabela.innerHTML = dados.ultimasVendas.map((v) => {
          const quantidadeExibida = (v.quantidade !== null && v.quantidade !== undefined) ? v.quantidade : '—';
          return `
          <tr>
            <td>${Util.formatarDataHora(v.dataVenda)}</td>
            <td>${v.clienteNome || '—'}</td>
            <td>${v.produtoNome || 'Produto removido'}</td>
            <td class="numerico">${quantidadeExibida}</td>
            <td class="numerico">${Util.formatarMoeda(v.valorTotal || 0)}</td>
          </tr>`;
        }).join('');
      }
    }
  } catch (erro) {
    Util.exibirToast('Erro ao carregar painel: ' + erro.message, 'erro');
  }
});