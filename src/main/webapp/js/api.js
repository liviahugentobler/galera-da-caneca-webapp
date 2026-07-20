/* ============================================================
   API.JS — camada de acesso à API REST do back-end (fetch),
   substitui o antigo dados.js baseado em localStorage.
   Mantém o mesmo "nome" GC para minimizar mudanças nas telas.
   ============================================================ */

const GC = (() => {
  const BASE = 'api';

  async function requisitar(caminho, opcoes = {}) {
    const resposta = await fetch(`${BASE}${caminho}`, {
      headers: { 'Content-Type': 'application/json' },
      ...opcoes,
    });
    const texto = await resposta.text();
    const corpo = texto ? JSON.parse(texto) : null;
    if (!resposta.ok) {
      const mensagem = (corpo && corpo.erro) ? corpo.erro : `Erro ${resposta.status}`;
      throw new Error(mensagem);
    }
    return corpo;
  }

  /* -------- Autenticação -------- */
  const auth = {
    async login(email, senha) {
      return requisitar('/auth/login', { method: 'POST', body: JSON.stringify({ email, senha }) });
    },
  };

  /* -------- Sessão (guardada no navegador após login) -------- */
  const sessao = {
    logar(usuario) {
      sessionStorage.setItem('gc_sessao_atual', JSON.stringify(usuario));
    },
    atual() {
      const bruto = sessionStorage.getItem('gc_sessao_atual');
      return bruto ? JSON.parse(bruto) : null;
    },
    sair() {
      sessionStorage.removeItem('gc_sessao_atual');
    },
  };

  /* -------- Vendedores -------- */
  const vendedores = {
    listar: (nome = '') => requisitar(`/vendedores${nome ? `?nome=${encodeURIComponent(nome)}` : ''}`),
    buscarPorId: (id) => requisitar(`/vendedores/${id}`),
    salvar(vendedor) {
      return vendedor.id
        ? requisitar(`/vendedores/${vendedor.id}`, { method: 'PUT', body: JSON.stringify(vendedor) })
        : requisitar('/vendedores', { method: 'POST', body: JSON.stringify(vendedor) });
    },
    remover: (id) => requisitar(`/vendedores/${id}`, { method: 'DELETE' }),
  };

  /* -------- Produtos -------- */
  const produtos = {
    listar: (nome = '') => requisitar(`/produtos${nome ? `?nome=${encodeURIComponent(nome)}` : ''}`),
    buscarPorId: (id) => requisitar(`/produtos/${id}`),
    salvar(produto) {
      return produto.id
        ? requisitar(`/produtos/${produto.id}`, { method: 'PUT', body: JSON.stringify(produto) })
        : requisitar('/produtos', { method: 'POST', body: JSON.stringify(produto) });
    },
    remover: (id) => requisitar(`/produtos/${id}`, { method: 'DELETE' }),
  };

  /* -------- Clientes -------- */
  const clientes = {
    listar: (nome = '') => requisitar(`/clientes${nome ? `?nome=${encodeURIComponent(nome)}` : ''}`),
    buscarPorId: (id) => requisitar(`/clientes/${id}`),
    salvar(cliente) {
      return cliente.id
        ? requisitar(`/clientes/${cliente.id}`, { method: 'PUT', body: JSON.stringify(cliente) })
        : requisitar('/clientes', { method: 'POST', body: JSON.stringify(cliente) });
    },
    remover: (id) => requisitar(`/clientes/${id}`, { method: 'DELETE' }),
  };

  /* -------- Vendas -------- */
  const vendas = {
    listar: (filtros = {}) => {
      const params = new URLSearchParams();
      if (filtros.vendedorId) params.set('vendedorId', filtros.vendedorId);
      if (filtros.cliente) params.set('cliente', filtros.cliente);
      const query = params.toString();
      return requisitar(`/vendas${query ? `?${query}` : ''}`);
    },
    buscarPorId: (id) => requisitar(`/vendas/${id}`),
    registrar: (venda) => requisitar('/vendas', { method: 'POST', body: JSON.stringify(venda) }),
    remover: (id) => requisitar(`/vendas/${id}`, { method: 'DELETE' }),
  };

  /* -------- Dashboard -------- */
  const dashboard = {
    metricas(vendedorId, isGerente) {
      const params = new URLSearchParams();
      params.set('isGerente', isGerente ? 'true' : 'false');
      if (vendedorId) params.set('vendedorId', vendedorId);
      return requisitar(`/dashboard?${params.toString()}`);
    },
  };

  /* -------- Regra de desconto (apenas para pré-visualização no formulário;
     o valor final é sempre recalculado e validado no back-end) -------- */
  function calcularVenda(precoUnitario, quantidade) {
    const subtotal = precoUnitario * quantidade;
    let percentualDesconto = 0;
    if (quantidade >= 10) percentualDesconto = 10;
    else if (quantidade >= 5) percentualDesconto = 5;
    const valorDesconto = subtotal * (percentualDesconto / 100);
    const total = subtotal - valorDesconto;
    return { subtotal, percentualDesconto, valorDesconto, total };
  }

  return { auth, sessao, vendedores, produtos, clientes, vendas, dashboard, calcularVenda };
})();
