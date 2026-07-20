# Evidências de Testes — Etapa 9 (Galera da Caneca)

## 1. Testes automatizados (JUnit 5)

O projeto já trazia, da Etapa 7, os seguintes testes automatizados, mantidos e
válidos após a integração web:

| Classe | Testes | O que verifica |
|---|---|---|
| `service.CalculadoraDeVendaTest` | 7 | Cálculo de subtotal, percentual de desconto (0%/5%/10%) por faixa de quantidade, valor total com desconto e validações de entrada (preço ≤ 0, quantidade ≤ 0). |
| `model.VendedorTest` | 4 | Regra de negócio `isGerente()` a partir do `Cargo` associado. |

**Como executar e gerar a evidência real (a rodar no seu ambiente, com Maven e
internet disponíveis — este ambiente de geração do projeto não tem acesso ao
Maven Central nem a um MySQL, então os testes não puderam ser executados
aqui):**

```
mvn test
```

> 📌 **Ação para você:** rode o comando acima no NetBeans (ou terminal) e cole
> aqui o resultado (ou um print do painel de testes do NetBeans, que mostra ✅
> em verde para cada teste). Isso é o que deve entrar neste documento antes de
> enviar a entrega.

```
[Cole aqui a saída do "mvn test" ou o print do NetBeans]
```

## 2. Plano de testes manuais (integração front-end + back-end + banco)

Estes casos cobrem o fluxo ponta a ponta do sistema web já integrado. Execute
cada um localmente (Tomcat/GlassFish + MySQL rodando) e marque o resultado.
**Tire um print de cada linha marcada como "Passou" — essas imagens são a
evidência de teste pedida na entrega.**

| # | Caso de teste | Passos | Resultado esperado | Resultado |
|---|---|---|---|---|
| CT01 | Login com credenciais válidas | Acessar `index.html`, informar e-mail/senha de um vendedor cadastrado | Redireciona para `dashboard.html` com o nome do usuário no cabeçalho | ☐ Passou ☐ Falhou |
| CT02 | Login com senha incorreta | Informar e-mail válido e senha errada | Mensagem de erro "E-mail ou senha inválidos", sem redirecionar | ☐ Passou ☐ Falhou |
| CT03 | Restrição de rota por perfil | Logar como Vendedor(a) e tentar abrir `produtos.html` diretamente pela URL | Redireciona para `dashboard.html` (sem acesso) | ☐ Passou ☐ Falhou |
| CT04 | Cadastro de cliente | Gerente ou vendedor cadastra um novo cliente com todos os campos | Cliente aparece na listagem `clientes.html` e no banco (`SELECT * FROM clientes`) | ☐ Passou ☐ Falhou |
| CT05 | Cadastro de produto | Gerente cadastra um novo produto com preço e estoque inicial | Produto aparece em `produtos.html` e no banco | ☐ Passou ☐ Falhou |
| CT06 | Cadastro de funcionário (gerente) | Gerente cadastra um novo funcionário marcando "É gerente" | Novo funcionário aparece com selo "Gerente" e consegue logar | ☐ Passou ☐ Falhou |
| CT07 | Venda com desconto de 0% | Registrar venda com quantidade = 3 | Total = preço × 3, sem desconto | ☐ Passou ☐ Falhou |
| CT08 | Venda com desconto de 5% | Registrar venda com quantidade = 7 | Total com 5% de desconto aplicado | ☐ Passou ☐ Falhou |
| CT09 | Venda com desconto de 10% | Registrar venda com quantidade = 12 | Total com 10% de desconto aplicado | ☐ Passou ☐ Falhou |
| CT10 | Baixa de estoque após venda | Conferir estoque do produto antes e depois de uma venda | Estoque reduzido exatamente na quantidade vendida | ☐ Passou ☐ Falhou |
| CT11 | Bloqueio por estoque insuficiente | Tentar vender quantidade maior que o estoque disponível | Sistema recusa com mensagem "Estoque insuficiente" | ☐ Passou ☐ Falhou |
| CT12 | Exclusão de venda devolve estoque | Excluir uma venda registrada e conferir o estoque do produto | Estoque volta a somar a quantidade da venda excluída | ☐ Passou ☐ Falhou |
| CT13 | Edição de funcionário sem alterar senha | Editar um funcionário deixando o campo senha em branco | Senha antiga é mantida no banco | ☐ Passou ☐ Falhou |
| CT14 | Exclusão de produto | Excluir um produto sem vendas associadas | Produto removido da listagem e do banco | ☐ Passou ☐ Falhou |
| CT15 | Painel (dashboard) reflete os dados reais | Comparar métricas do painel com `SELECT` direto no banco | Total vendido, nº de vendas e unidades batem com o banco | ☐ Passou ☐ Falhou |

## 3. Revisão estática de código (feita durante a integração)

Além dos testes acima (a serem executados por você), a integração desta etapa
passou por uma checagem manual de compatibilidade entre as camadas, descrita
no documento `BUGTRACKING.md`. Essa checagem já identificou e corrigiu 6
inconsistências entre o front-end (Etapa 8) e o modelo de dados do back-end
(Etapa 6) antes mesmo da primeira execução — ver detalhes lá.

## 4. Limitação conhecida (documentar na entrega)

A entidade `Venda` do banco de dados fornecido não possui uma coluna de
data/hora da venda. Por isso, as telas não exibem "data da venda" — apenas
cliente, produto, quantidade, desconto e vendedor. Sugerimos, como melhoria
futura, adicionar uma coluna `data_hora DATETIME DEFAULT CURRENT_TIMESTAMP`.
