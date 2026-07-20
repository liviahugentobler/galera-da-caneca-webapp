# Galera da Caneca — Front-end Web (Etapa 8)

Front-end estático (HTML + CSS + JS) do sistema de vendas **Galera da Caneca**,
desenvolvido a partir das telas desktop (Swing) do projeto, para a etapa de
front-end da disciplina. **Não há conexão com back-end/banco de dados** — os
dados são simulados no navegador com `localStorage`/`sessionStorage`, apenas
para permitir que as telas funcionem de forma dinâmica.

## Estrutura de pastas

```
galera-da-caneca-frontend/
├── index.html              → Login
├── dashboard.html           → Painel principal (métricas e atalhos)
├── vendas.html               → Listagem de vendas (com busca e filtro)
├── venda-form.html          → Cadastro de venda (cálculo automático de desconto)
├── produtos.html             → Catálogo de produtos (somente gerente)
├── produto-form.html        → Cadastro/edição de produto (somente gerente)
├── vendedores.html           → Funcionários cadastrados (somente gerente)
├── vendedor-form.html       → Cadastro/edição de funcionário (somente gerente)
├── css/
│   ├── base.css              → reset, variáveis de cor/tipografia
│   ├── layout.css            → cabeçalho, navegação, estrutura de página
│   └── components.css        → botões, formulários, tabelas, cartões, alertas
├── js/
│   ├── dados.js               → camada de dados simulada (localStorage) + regra de desconto
│   ├── utilitarios.js         → formatação, validação, toasts, proteção de rota
│   ├── cabecalho.js           → nome do usuário, permissões por perfil, logout
│   ├── login.js                → validação e autenticação simulada
│   ├── painel.js               → métricas do dashboard
│   ├── produtos.js             → listagem/formulário de produtos
│   ├── vendedores.js           → listagem/formulário de funcionários
│   └── vendas.js               → listagem/formulário de vendas
└── wireframes/
    ├── wireframes.html         → wireframes de baixa fidelidade (fonte)
    └── wireframes.png          → wireframes exportados em imagem
```

## Como executar

Como é um projeto 100% estático, basta abrir `index.html` no navegador
(ou usar a extensão **Live Server** do VS Code, recomendado para evitar
restrições de `file://`).

## Login de demonstração

Como não há banco de dados, os "funcionários" já vêm pré-cadastrados no
`localStorage` na primeira execução (veja `js/api.js`, função `semear()`):

| Perfil    | E-mail                     | Senha |
|-----------|-----------------------------|-------|
| Gerente   | marcos.rezende@galeradacaneca.com | 1234  |
| Vendedora | juliana.cardoso@galeradacaneca.com | 1234  |

A própria tela de login tem botões de preenchimento automático dessas
credenciais.

## Principais funcionalidades dinâmicas (JS)

- **Login e sessão**: autenticação simulada, proteção de rotas
  (`Util.protegerRota`) e controle de acesso por perfil (Vendedor x Gerente).
- **Regra de desconto por quantidade** (equivalente à `CalculadoraDeVenda` do
  back-end): 0% até 4 unidades, 5% de 5 a 9, 10% a partir de 10 — calculada
  em tempo real no formulário de venda, com resumo do pedido.
- **CRUD simulado** de Vendas, Produtos e Funcionários via `localStorage`.
- **Validações de formulário**: e-mail, CPF (com máscara), telefone (com
  máscara), senha mínima, estoque disponível, campos obrigatórios.
- **Busca e filtros** nas listagens (cliente, produto, funcionário).
- **Controle de visibilidade por perfil**: itens de menu e ações
  (`.somente-gerente`) só aparecem para o perfil Gerente.

## Observações para a próxima etapa

Este projeto não implementa nenhuma chamada a back-end/banco de dados,
conforme pedido no enunciado. A integração com a API/banco (Etapas
seguintes) deverá substituir a camada `js/api.js` por chamadas reais,
mantendo a mesma interface de funções (`GC.vendedores`, `GC.produtos`,
`GC.vendas`) para minimizar o retrabalho nas telas.
