# Bugtracking — Etapa 9 (Galera da Caneca)

Registro dos problemas encontrados durante a integração do front-end (Etapa 8)
com o back-end (Etapa 6/7) e suas correções. Formato inspirado em ferramentas
como Trello/GitHub Issues — pode ser recriado em qualquer uma delas como
evidência visual (recomendado: criar um board no Trello ou os Issues no
próprio repositório GitHub, um card/issue por linha abaixo).

| ID | Título | Descrição | Severidade | Status | Correção aplicada |
|---|---|---|---|---|---|
| BUG-01 | Campo "categoria" inexistente no banco | O formulário de produto (Etapa 8) tinha um campo "Categoria" que não existe na tabela `produtos` nem na entidade `Produto`. | Média | ✅ Corrigido | Campo removido do `produto-form.html`; formulário passou a usar apenas `nomeProd`, `preco` e `quantidade`, que existem na entidade. |
| BUG-02 | Campo "telefone" inexistente em Vendedor | O formulário de funcionário (Etapa 8) pedia telefone, mas a entidade `Vendedor` não tem essa coluna. | Baixa | ✅ Corrigido | Campo removido de `vendedor-form.html`; adicionado em seu lugar o campo "Sexo" (M/F), que a entidade de fato possui. |
| BUG-03 | Venda não registrava a quantidade vendida | A tabela `vendas` só guardava `valor_total`, sem a quantidade de itens. Isso impedia mostrar a quantidade nas listagens e recalcular o desconto para exibição. | Alta | ✅ Corrigido | Adicionada a coluna `quantidade` em `vendas` (`database/migracao_etapa9.sql`) e o campo correspondente na entidade `Venda`. |
| BUG-04 | `VendaService` não permitia excluir venda | A interface de serviço não expunha um método de exclusão, impossibilitando o botão "Excluir" da tela de vendas. | Alta | ✅ Corrigido | Adicionado `excluir(int id)` em `VendaService`/`VendaServiceImpl`, delegando ao `VendaDAO` (que já suportava a operação via `GenericDAO`). |
| BUG-05 | Exclusão de venda falharia por restrição de chave estrangeira | A tabela `pagamentos` referencia `vendas` sem `ON DELETE CASCADE`. Excluir uma venda com pagamento associado geraria erro de integridade referencial. | Alta | ✅ Corrigido | `VendaServlet.doDelete` agora localiza e exclui o `Pagamento` associado **antes** de excluir a `Venda`. |
| BUG-06 | Tela de venda usava nome de cliente como texto livre | O formulário de venda (Etapa 8, sem back-end) apenas pedia o nome do cliente digitado, sem vínculo com um cadastro real — mas o banco já tem uma tabela `clientes` completa. | Alta | ✅ Corrigido | Criado o CRUD completo de Clientes (`clientes.html`, `cliente-form.html`, `ClienteServlet`) e o formulário de venda passou a usar um `<select>` vinculado a um cliente cadastrado (`clienteId`). |
| BUG-07 | Ausência de coluna de data/hora na venda | A entidade `Venda` não guarda quando a venda ocorreu, então o painel/listagens não podiam exibir essa informação. | Baixa | ⚠️ Não corrigido (fora do escopo desta entrega) | Documentado como limitação conhecida em `TESTES_EVIDENCIAS.md`, com sugestão de melhoria futura (`data_hora DATETIME DEFAULT CURRENT_TIMESTAMP`). |

## Como usar este documento como evidência

1. **Opção rápida:** entregue este arquivo (`BUGTRACKING.md`) junto com o
   projeto — ele já serve como registro de bugtracking.
2. **Opção recomendada (mais próxima de uma ferramenta real):** crie um board
   gratuito no [Trello](https://trello.com) com 3 colunas — *Encontrado*,
   *Em correção*, *Corrigido* — e recrie os 7 cards acima. Tire um print do
   board final (todos os cards em "Corrigido", exceto o BUG-07) e inclua na
   entrega.
3. **Alternativa:** use o **GitHub Issues** do repositório criado no passo de
   versionamento (uma issue por bug, fechada quando corrigida) — o próprio
   histórico de issues fechadas já é a evidência.
