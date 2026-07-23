/* ============================================================
   CLIENTES.JS — listagem e formulário de clientes via API REST
   ============================================================ */

async function inicializarListaClientes() {
  const sessao = Util.protegerRota();
  if (!sessao) return;

  const corpoTabela = document.querySelector('#tabela-clientes tbody');
  const campoBusca = document.querySelector('#busca-cliente');
  const contador = document.querySelector('[data-contador-clientes]');

  async function renderizar() {
    try {
      const termoBusca = campoBusca ? campoBusca.value : '';
      const lista = await GC.clientes.listar(termoBusca || '');
      if (contador) contador.textContent = `${lista.length} cliente(s)`;

      if (lista.length === 0) {
        corpoTabela.innerHTML = `<tr><td colspan="6"><div class="estado-vazio"><h3>Nenhum cliente encontrado</h3><p>Ajuste a busca ou cadastre um novo cliente.</p></div></td></tr>`;
        return;
      }

      corpoTabela.innerHTML = lista.map((c) => `
        <tr>
          <td>${c.id}</td>
          <td>${c.nomeCompleto}</td>
          <td>${c.cpf}</td>
          <td>${c.telefone || '—'}</td>
          <td>${c.email || '—'}</td>
          <td>
            <div class="col-acoes">
              <a class="btn btn--secundario btn--pequeno" href="cliente-form.html?id=${c.id}">Editar</a>
              <button class="btn btn--perigo btn--pequeno" data-excluir="${c.id}" type="button">Excluir</button>
            </div>
          </td>
        </tr>`).join('');

      corpoTabela.querySelectorAll('[data-excluir]').forEach((botao) => {
        botao.addEventListener('click', async () => {
          if (!confirm('Deseja realmente excluir este cliente?')) return;
          try {
            await GC.clientes.remover(botao.dataset.excluir);
            Util.exibirToast('Cliente excluído com sucesso.', 'sucesso');
            renderizar();
          } catch (erro) {
            Util.exibirToast('Erro: ' + erro.message, 'erro');
          }
        });
      });
    } catch (erro) {
      Util.exibirToast('Erro ao carregar clientes: ' + erro.message, 'erro');
    }
  }

  if (campoBusca) campoBusca.addEventListener('input', renderizar);
  renderizar();
}

async function inicializarFormularioCliente() {
  const sessao = Util.protegerRota();
  if (!sessao) return;

  const idParam = Util.parametroURL('id');
  const form = document.querySelector('#form-cliente');
  const titulo = document.querySelector('[data-titulo-form]');
  const campoId = document.querySelector('#clienteId');
  const campoNome = document.querySelector('#nome');
  const campoCPF = document.querySelector('#cpf');
  const campoNascimento = document.querySelector('#dataNascimento');
  const campoTelefone = document.querySelector('#telefone');
  const campoSexo = document.querySelector('#sexo');
  const campoEmail = document.querySelector('#email');
  const campoEndereco = document.querySelector('#endereco');
  const campoSenha = document.querySelector('#senha');

  Util.mascararCPF(campoCPF);
  Util.mascararTelefone(campoTelefone);

  if (idParam) {
    try {
      const cliente = await GC.clientes.buscarPorId(idParam);
      titulo.textContent = 'Editar Cliente';
      if (campoId) campoId.value = cliente.id;
      campoNome.value = cliente.nomeCompleto;
      campoCPF.value = cliente.cpf;
      campoNascimento.value = cliente.nascimento || '';
      campoTelefone.value = cliente.telefone || '';
      campoSexo.value = cliente.sexo || 'M';
      campoEmail.value = cliente.email || '';
      campoEndereco.value = cliente.endereco || '';
      campoSenha.placeholder = 'Deixe em branco para manter a senha atual';
      campoSenha.required = false;
    } catch (erro) {
      Util.exibirToast('Erro ao carregar cliente: ' + erro.message, 'erro');
    }
  }

  form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    let valido = true;

    if (campoNome.value.trim().length < 3) { Util.marcarInvalido(campoNome, 'Informe o nome completo.'); valido = false; }
    else Util.limparInvalido(campoNome);

    if (!Util.validarCPF(campoCPF.value.trim())) { Util.marcarInvalido(campoCPF, 'Formato esperado: 000.000.000-00.'); valido = false; }
    else Util.limparInvalido(campoCPF);

    if (!idParam && campoSenha.value.trim().length < 4) { Util.marcarInvalido(campoSenha, 'A senha deve ter ao menos 4 caracteres.'); valido = false; }
    else Util.limparInvalido(campoSenha);

    if (!valido) return;

    try {
      const senhaDigitada = campoSenha.value.trim();
      const idCliente = campoId && campoId.value ? Number(campoId.value) : null;
      const cpfLimpo = campoCPF.value.replace(/\D/g, '');

      await GC.clientes.salvar({
        id: idCliente,
        nomeCompleto: campoNome.value.trim(),
        cpf: cpfLimpo,
        nascimento: campoNascimento.value || null,
        telefone: campoTelefone.value.trim(),
        sexo: campoSexo.value,
        email: campoEmail.value.trim(),
        endereco: campoEndereco.value.trim(),
        senha: senhaDigitada ? senhaDigitada : undefined,
      });
      Util.exibirToast('Cliente salvo com sucesso.', 'sucesso');
      window.location.href = 'clientes.html';
    } catch (erro) {
      Util.exibirToast('Erro ao salvar cliente: ' + erro.message, 'erro');
    }
  });
}