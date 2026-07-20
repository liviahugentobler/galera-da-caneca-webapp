# Bugtracking — Etapa 9 (Galera da Caneca)

Registro dos defeitos encontrados durante a integração do back-end (JPA/MySQL)
com o front-end (Etapa 8) e a construção da camada web (Servlets). Formato
inspirado em uma ferramenta de bugtracking simples (tipo Trello/GitHub
Issues): **ID, Severidade, Onde foi encontrado, Descrição, Causa, Correção,
Status**.

> Para a entrega, tire um print desta tabela preenchida (ou do quadro
> equivalente no Trello/GitHub Issues/Jira, se preferir usar uma ferramenta
> visual) para anexar como "evidências com registros do bugtracking".

---

### BUG-01 — Coluna `senha` como `INT UNIQUE`
- **Severidade:** Alta
- **Onde:** `database/GaleraDaCaneca_database.sql` (tabelas `vendedores` e `clientes`)
- **Descrição:** A coluna `senha` foi criada como `INT NOT NULL UNIQUE`. Isso
  impede senhas alfanuméricas (ex.: `"minhaSenha123"` não é um número) e
  impede que dois usuários diferentes tenham a mesma senha — o que não é
  (e não deveria ser) uma regra do sistema.
- **Como foi encontrado:** Ao testar o cadastro de um novo funcionário pela
  tela web com senha `"1234abcd"`, o INSERT falharia no banco.
- **Correção:** `database/ajustes_etapa9.sql` altera a coluna para
  `VARCHAR(30)` e remove a constraint `UNIQUE` em ambas as tabelas.
- **Status:** ✅ Corrigido

### BUG-02 — Tabela `vendas` não guardava a quantidade vendida
- **Severidade:** Alta
- **Onde:** `database/GaleraDaCaneca_database.sql` (tabela `vendas`) e
  `model/Venda.java`
- **Descrição:** Só o `valor_total` era persistido. Sem a quantidade, não é
  possível reexibir o desconto aplicado (`CalculadoraDeVenda`) numa venda já
  registrada, nem validar estoque a partir do histórico.
- **Correção:** Adicionada a coluna `quantidade` (`ajustes_etapa9.sql`) e o
  campo `quantidade` na entidade `Venda`, populado pelo `VendaServlet` a cada
  nova venda.
- **Status:** ✅ Corrigido

### BUG-03 — Tabela `vendas` sem data/hora
- **Severidade:** Média
- **Onde:** `database/GaleraDaCaneca_database.sql` (tabela `vendas`)
- **Descrição:** Não havia coluna de data, impossibilitando ordenar o
  histórico de vendas por período ou exibir "quando" a venda ocorreu — a tela
  de listagem de vendas (Etapa 8) já previa uma coluna "Data".
- **Correção:** Adicionada a coluna `data_venda` (`ajustes_etapa9.sql`) e o
  campo `dataVenda` na entidade `Venda`, preenchido com a data/hora do
  servidor no momento do registro.
- **Status:** ✅ Corrigido

### BUG-04 — Namespace incorreto na propriedade de senha do JDBC
- **Severidade:** Alta (bloqueante)
- **Onde:** `src/main/resources/META-INF/persistence.xml`
- **Descrição:** Todas as propriedades de conexão usavam o prefixo
  `javax.persistence.jdbc.*`, exceto a senha, que usava
  `jakarta.persistence.jdbc.password`. Como o projeto depende de
  `javax.persistence-api` (não `jakarta`), essa propriedade era **ignorada**
  pelo Hibernate, o que tende a causar falha de autenticação com o MySQL.
- **Como foi encontrado:** Inspeção de código durante a integração —
  inconsistência entre as chaves de propriedade.
- **Correção:** Propriedade renomeada para `javax.persistence.jdbc.password`,
  consistente com as demais.
- **Status:** ✅ Corrigido

### BUG-05 — Front-end (Etapa 8) sem tela de Clientes
- **Severidade:** Média
- **Onde:** front-end (Etapa 8)
- **Descrição:** O banco já possuía a entidade `Cliente` (necessária para
  registrar uma venda, já que `vendas.id_cliente` é obrigatório), mas as
  telas web da Etapa 8 não incluíam nenhum cadastro de clientes — a tela de
  nova venda só pedia um nome livre em texto, que não correspondia a nenhum
  cliente cadastrado no banco.
- **Correção:** Criadas as páginas `clientes.html` e `cliente-form.html`
  (CRUD completo), e o formulário de venda passou a exigir a seleção de um
  cliente já cadastrado (`<select>` populado via `GET /api/clientes`).
- **Status:** ✅ Corrigido

### BUG-06 — Campo "Categoria" no formulário de produto sem coluna correspondente
- **Severidade:** Baixa
- **Onde:** front-end (Etapa 8), `produto-form.html`
- **Descrição:** O formulário de cadastro de produto tinha um campo
  "Categoria" (Caneca/Growler/Equipamento), mas a tabela `produtos` não tem
  essa coluna — o valor era descartado silenciosamente na versão simulada
  com `localStorage` da Etapa 8, e passaria a gerar erro (campo
  desconhecido) ao integrar com o back-end real.
- **Correção:** Campo removido do formulário nesta etapa. Fica registrado
  como sugestão de melhoria futura (adicionar coluna `categoria` ao banco,
  se o professor validar a necessidade).
- **Status:** ✅ Corrigido (removido) — melhoria futura documentada

---

## Como usar este documento na entrega

1. Preencha/ajuste os status conforme for reexecutando os testes localmente.
2. Se preferir uma ferramenta visual (recomendado pelo enunciado), recrie
   estas 6 entradas como *issues* no GitHub do novo repositório (Etapa 9) ou
   como cartões num quadro Trello, e tire um print do quadro/lista de issues
   fechadas para anexar à entrega.
3. Qualquer bug novo encontrado ao rodar os testes da Etapa 7 (próxima
   seção/documento) deve ser adicionado aqui com o próximo ID (`BUG-07`,
   `BUG-08`, ...).
