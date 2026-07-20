# Galera da Caneca — Projeto Web Integrado (Etapa 9)

Sistema de vendas **Galera da Caneca**: aplicação Java Web completa, com
front-end (HTML/CSS/JS) integrado a um back-end em **Servlets + JPA/Hibernate
+ MySQL**, seguindo arquitetura em camadas (Model → DAO → Service → Servlet)
e princípios SOLID.

## Arquitetura

```
Navegador (HTML/CSS/JS em src/main/webapp)
        │  fetch() → JSON
        ▼
Servlets REST (br.com.galeradacaneca.web.servlet)  ── "Controller"
        │
        ▼
Services (br.com.galeradacaneca.service)           ── regras de negócio
        │
        ▼
DAOs (br.com.galeradacaneca.dao)                    ── acesso a dados (JPA)
        │
        ▼
MySQL (banco Galera_da_Caneca)
```

## Pré-requisitos

- Java 17+
- Maven (com acesso ao Maven Central)
- MySQL 8 rodando localmente
- Um servidor de aplicação compatível com Servlet 4.0 (Tomcat 9/10 ou
  GlassFish) — no NetBeans, normalmente já vem configurado

## Como rodar

1. **Banco de dados** — no MySQL Workbench (ou similar), rode:
   - `database/GaleraDaCaneca_database.sql`, se for criar o banco do zero; **ou**
   - `database/ajustes_etapa9.sql`, se o banco das etapas anteriores já existir
     (ele só aplica as correções e novas colunas desta etapa — ver
     `evidencias/bugtracking.md` para o motivo de cada uma).

2. **Configuração de conexão** — confira/ajuste usuário e senha do MySQL em
   `src/main/resources/META-INF/persistence.xml`.

3. **Build e deploy** — abra o projeto no NetBeans (Maven Web Application) e
   rode normalmente ("Run"), ou via linha de comando:
   ```bash
   mvn clean package
   ```
   Isso gera `target/galera-da-caneca.war`, que pode ser implantado em
   qualquer Tomcat/GlassFish.

4. **Acesso** — com o servidor local na porta padrão:
   ```
   http://localhost:8080/galera-da-caneca/
   ```
   (o NetBeans já abre essa URL automaticamente ao rodar o projeto)

## Login de demonstração

O banco de dados de exemplo já vem com funcionários cadastrados (ver
`INSERT INTO vendedores` no script SQL). Use um e-mail/senha cadastrado nesse
script para entrar — lembre que, após o `ajustes_etapa9.sql`, o campo senha
aceita texto normal (não precisa mais ser um número).

## Estrutura do projeto

```
├── pom.xml                         → empacotamento WAR, dependências (Hibernate, MySQL, Servlet, Gson)
├── database/
│   ├── GaleraDaCaneca_database.sql → schema completo, já corrigido (banco novo)
│   └── ajustes_etapa9.sql          → migração para quem já tinha o banco das etapas anteriores
├── evidencias/
│   ├── bugtracking.md              → bugs encontrados e corrigidos durante a integração
│   └── versionamento.md            → evidências e passo a passo do versionamento
├── TESTES_EVIDENCIAS.md            → testes automatizados (JUnit) + plano de testes manuais
└── src/
    ├── main/java/br/com/galeradacaneca/
    │   ├── model/     → entidades JPA (Cliente, Vendedor, Cargo, Produto, Venda, Pagamento)
    │   ├── dao/       → acesso a dados (interfaces + impl/)
    │   ├── service/   → regras de negócio (interfaces + impl/) e CalculadoraDeVenda
    │   └── web/
    │       ├── AppContext.java     → composição das dependências (DAO → Service)
    │       ├── servlet/            → API REST (/api/...)
    │       └── util/JsonUtil.java  → leitura/escrita de JSON nas requisições
    ├── main/webapp/    → front-end (HTML/CSS/JS), servido estaticamente pelo próprio Tomcat
    └── test/java/      → testes JUnit 5 (CalculadoraDeVendaTest, VendedorTest)
```

## Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Autentica (`{email, senha}`) |
| GET/POST | `/api/vendedores` | Lista / cadastra funcionários |
| GET/PUT/DELETE | `/api/vendedores/{id}` | Consulta / atualiza / exclui |
| GET/POST | `/api/produtos` | Lista / cadastra produtos |
| GET/PUT/DELETE | `/api/produtos/{id}` | Consulta / atualiza / exclui |
| GET/POST | `/api/clientes` | Lista / cadastra clientes |
| GET/PUT/DELETE | `/api/clientes/{id}` | Consulta / atualiza / exclui |
| GET/POST | `/api/vendas` | Lista / registra vendas (calcula desconto e dá baixa no estoque) |
| GET/DELETE | `/api/vendas/{id}` | Consulta / exclui (devolve estoque) |
| GET | `/api/dashboard` | Métricas agregadas do painel |

## Documentação da entrega

- **Testes:** `TESTES_EVIDENCIAS.md`
- **Bugtracking:** `evidencias/bugtracking.md`
- **Versionamento:** `evidencias/versionamento.md`
- **Histórico do front-end (Etapa 8):** `src/main/webapp/README_ETAPA8_HISTORICO.md`
