/* ============================================================
   VENDEDORES.JS — listagem e formulário de funcionários via API
   ============================================================ */

async function inicializarListaVendedores() {
  const sessao = Util.protegerRota({ exigirGerente: true });
  if (!sessao) return;

  const corpoTabela = document.querySelector('#tabela-vendedores tbody');
  const campoBusca = document.querySelector('#busca-vendedor');
  const contador = document.querySelector('[data-contador-vendedores]');

  async function renderizar() {
    try {
      const lista = await GC.vendedores.listar(campoBusca?.value || '');
      if (contador) contador.textContent = `${lista.length} funcionário(s)`;

      if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="6"><div class="estado-vazio"><h3>Nenhum funcionário encontrado</h3><p>Ajuste a busca ou cadastre um novo funcionário.</p></div></td></tr>`;
        return;
      }

      corpoTabela.innerHTML = lista.map((v) => `
        <tr>
          <td>${v.id}</td>
          <td>${v.nomeCompleto}</td>
          <td>${v.cpf}</td>
          <td>${v.email}</td>
          <td><span class="selo ${v.isGerente ? 'selo--gerente' : 'selo--vendedor'}">${v.isGerente ? 'Gerente' : 'Vendedor(a)'}</span></td>
          <td>
            <div class="col-acoes">
              <a class="btn btn--secundario btn--pequeno" href="vendedor-form.html?id=${v.id}">Editar</a>
              <button class="btn btn--perigo btn--pequeno" data-excluir="${v.id}" type="button" ${v.id === sessao.id ? 'disabled title="Não é possível excluir o próprio usuário"' : ''}>Excluir</button>
            </div>
          </td>
        </tr>`).join('');

      corpoTabela.querySelectorAll('[data-excluir]').forEach((botao) => {
        botao.addEventListener('click', async () => {
          if (!confirm('Deseja realmente excluir este funcionário?')) return;
          try {
            await GC.vendedores.remover(botao.dataset.excluir);
            Util.exibirToast('Funcionário excluído com sucesso.', 'sucesso');
            renderizar();
          } catch (erro) {
            Util.exibirToast('Erro: ' + erro.message, 'erro');
          }
        });
      });
    } catch (erro) {
      Util.exibirToast('Erro ao carregar funcionários: ' + erro.message, 'erro');
    }
  }

  campoBusca?.addEventListener('input', renderizar);
  renderizar();
}

async function inicializarFormularioVendedor() {
  const sessao = Util.protegerRota({ exigirGerente: true });
  if (!sessao) return;

  const idParam = Util.parametroURL('id');
  const form = document.querySelector('#form-vendedor');
  const titulo = document.querySelector('[data-titulo-form]');
  const campoId = document.querySelector('#vendedorId');
  const campoNome = document.querySelector('#nome');
  const campoCPF = document.querySelector('#cpf');
  const campoNascimento = document.querySelector('#dataNascimento');
  const campoSexo = document.querySelector('#sexo');
  const campoEmail = document.querySelector('#email');
  const campoSenha = document.querySelector('#senha');
  const campoGerente = document.querySelector('#isGerente');

  Util.mascararCPF(campoCPF);

  if (idParam) {
    try {
      const vendedor = await GC.vendedores.buscarPorId(idParam);
      titulo.textContent = 'Editar Funcionário';
      campoId.value = vendedor.id;
      campoNome.value = vendedor.nomeCompleto;
      campoCPF.value = vendedor.cpf;
      campoNascimento.value = vendedor.nascimento || '';
      if (campoSexo) campoSexo.value = vendedor.sexo || 'M';
      campoEmail.value = vendedor.email;
      campoGerente.checked = !!vendedor.isGerente;
      campoSenha.placeholder = 'Deixe em branco para manter a senha atual';
      campoSenha.required = false;
    } catch (erro) {
      Util.exibirToast('Erro ao carregar funcionário: ' + erro.message, 'erro');
    }
  }

  form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    let valido = true;

    if (campoNome.value.trim().length < 3) { Util.marcarInvalido(campoNome, 'Informe o nome completo.'); valido = false; }
    else Util.limparInvalido(campoNome);

    if (!Util.validarCPF(campoCPF.value.trim())) { Util.marcarInvalido(campoCPF, 'Formato esperado: 000.000.000-00.'); valido = false; }
    else Util.limparInvalido(campoCPF);

    if (!Util.validarEmail(campoEmail.value.trim())) { Util.marcarInvalido(campoEmail, 'Informe um e-mail válido.'); valido = false; }
    else Util.limparInvalido(campoEmail);

    if (!idParam && campoSenha.value.trim().length < 4) { Util.marcarInvalido(campoSenha, 'A senha deve ter ao menos 4 caracteres.'); valido = false; }
    else Util.limparInvalido(campoSenha);

    if (!valido) return;

    try {
      await GC.vendedores.salvar({
        id: campoId.value ? Number(campoId.value) : null,
        nomeCompleto: campoNome.value.trim(),
        cpf: campoCPF.value.trim(),
        nascimento: campoNascimento.value || null,
        sexo: campoSexo ? campoSexo.value : null,
        email: campoEmail.value.trim(),
        senha: campoSenha.value.trim() || undefined,
        isGerente: campoGerente.checked,
      });
      Util.exibirToast('Funcionário salvo com sucesso.', 'sucesso');
      window.location.href = 'vendedores.html';
    } catch (erro) {
      Util.exibirToast('Erro ao salvar funcionário: ' + erro.message, 'erro');
    }
  });
}
