/* ============================================================
   CABECALHO.JS — inicializa o cabeçalho comum às páginas internas
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {
  const sessao = GC.sessao.atual();
  if (!sessao) return;

  const nomeEl = document.querySelector('[data-usuario-nome]');
  if (nomeEl) nomeEl.textContent = sessao.nomeCompleto || sessao.nome;

  const perfilEl = document.querySelector('[data-usuario-perfil]');
  if (perfilEl) perfilEl.textContent = sessao.isGerente ? 'Gerente' : 'Vendedor(a)';

  if (!sessao.isGerente) {
    document.querySelectorAll('.somente-gerente').forEach((el) => el.remove());
  }

  document.querySelectorAll('.nav-principal a').forEach((link) => {
    if (link.getAttribute('href') === window.location.pathname.split('/').pop()) {
      link.classList.add('ativo');
    }
  });

  const botaoSair = document.querySelector('[data-acao="sair"]');
  if (botaoSair) {
    botaoSair.addEventListener('click', () => {
      GC.sessao.sair();
      window.location.href = 'index.html';
    });
  }
});
