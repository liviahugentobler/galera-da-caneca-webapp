/* ============================================================
   UTILITARIOS.JS — formatação, validação e apoio de UI
   ============================================================ */

const Util = {
  formatarMoeda(valor) {
    return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  },

  formatarDataHora(isoString) {
    const data = new Date(isoString);
    if (Number.isNaN(data.getTime())) return isoString;
    return data.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  },

  validarEmail(valor) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor);
  },

  validarCPF(valor) {
    return /^\d{3}\.\d{3}\.\d{3}-\d{2}$/.test(valor);
  },

  mascararCPF(input) {
    input.addEventListener('input', () => {
      let v = input.value.replace(/\D/g, '').slice(0, 11);
      v = v.replace(/(\d{3})(\d)/, '$1.$2');
      v = v.replace(/(\d{3})(\d)/, '$1.$2');
      v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
      input.value = v;
    });
  },

  mascararTelefone(input) {
    input.addEventListener('input', () => {
      let v = input.value.replace(/\D/g, '').slice(0, 11);
      v = v.replace(/(\d{2})(\d)/, '($1) $2');
      v = v.replace(/(\d{5})(\d)/, '$1-$2');
      input.value = v;
    });
  },

  marcarInvalido(input, mensagem) {
    input.classList.add('invalido');
    const erro = input.closest('.campo')?.querySelector('.mensagem-erro');
    if (erro) {
      erro.textContent = mensagem;
      erro.classList.add('visivel');
    }
  },

  limparInvalido(input) {
    input.classList.remove('invalido');
    const erro = input.closest('.campo')?.querySelector('.mensagem-erro');
    if (erro) erro.classList.remove('visivel');
  },

  exibirToast(mensagem, tipo = 'sucesso') {
    let container = document.querySelector('.toast-container');
    if (!container) {
      container = document.createElement('div');
      container.className = 'toast-container';
      document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `alerta alerta--${tipo} toast`;
    toast.setAttribute('role', 'status');
    toast.textContent = mensagem;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3200);
  },


  protegerRota({ exigirGerente = false } = {}) {
    const sessao = GC.sessao.atual();
    if (!sessao) {
      window.location.href = 'index.html';
      return null;
    }
    if (exigirGerente && !sessao.isGerente) {
      window.location.href = 'dashboard.html';
      return null;
    }
    return sessao;
  },

  parametroURL(nome) {
    return new URLSearchParams(window.location.search).get(nome);
  },
};
