/* ============================================================
   LOGIN.JS — autenticação real via API (POST /api/auth/login)
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {
  if (GC.sessao.atual()) {
    window.location.href = 'dashboard.html';
    return;
  }

  const form = document.querySelector('#form-login');
  const campoEmail = document.querySelector('#email');
  const campoSenha = document.querySelector('#senha');
  const botaoEntrar = form.querySelector('button[type="submit"]');

  form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    let valido = true;

    if (!Util.validarEmail(campoEmail.value.trim())) {
      Util.marcarInvalido(campoEmail, 'Informe um e-mail válido.');
      valido = false;
    } else {
      Util.limparInvalido(campoEmail);
    }

    if (campoSenha.value.trim().length < 4) {
      Util.marcarInvalido(campoSenha, 'A senha deve ter ao menos 4 caracteres.');
      valido = false;
    } else {
      Util.limparInvalido(campoSenha);
    }

    if (!valido) return;

    botaoEntrar.disabled = true;
    botaoEntrar.textContent = 'Entrando…';

    try {
      const usuario = await GC.auth.login(campoEmail.value.trim(), campoSenha.value.trim());
      GC.sessao.logar(usuario);
      window.location.href = 'dashboard.html';
    } catch (erro) {
      Util.marcarInvalido(campoSenha, erro.message || 'Login ou senha inválidos.');
      botaoEntrar.disabled = false;
      botaoEntrar.textContent = 'Entrar';
    }
  });

  document.querySelectorAll('[data-preencher]').forEach((botao) => {
    botao.addEventListener('click', () => {
      campoEmail.value = botao.dataset.email;
      campoSenha.value = botao.dataset.senha;
    });
  });
});
