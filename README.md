# Galera da Caneca — Sistema de Gestão de Vendas 
> Projeto Integrador II — SENAC EAD  
> Responsável: Lívia Hugentobler  

Sistema de gestão de vendas para uma loja de canecas e produtos de bar,
desenvolvido em **Java** com persistência em **MySQL** via **JPA/Hibernate**,
seguindo uma arquitetura em camadas (**Model / DAO / Service**) orientada
pelos princípios **SOLID**.

Na Etapa 9, o front-end estático da Etapa 8 (HTML, CSS e JavaScript) foi
integrado ao back-end, transformando o projeto em uma aplicação **Java Web**
completa, empacotada como `.war` e servida por um servidor de aplicação
Jakarta EE, com uma **API REST** própria (`Servlets` + `Gson`) consumida pelo
front-end via `fetch`.

> ⚠️ O front-end não usa mais dados simulados em `localStorage`. Todas as
> telas consomem a API REST descrita neste documento. 

---

## Sumário

- [Arquitetura](#arquitetura)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do banco de dados](#configuração-do-banco-de-dados)
- [Configuração do servidor (Tomcat) no NetBeans](#configuração-do-servidor-tomcat-no-netbeans)
- [Executando o projeto](#executando-o-projeto)
- [Login de demonstração](#login-de-demonstração)
- [Estrutura de pastas](#estrutura-de-pastas)
- [API REST](#api-rest)
- [Regra de negócio: desconto por quantidade](#regra-de-negócio-desconto-por-quantidade)
- [Testes automatizados](#testes-automatizados)


---

## Arquitetura

O projeto segue uma arquitetura em camadas clássica, agora com uma camada
web adicional por cima do back-end já existente:

```
Front-end (HTML/CSS/JS)
        │  fetch("api/...")
        ▼
Servlets (web/servlet)   ──►  JsonUtil (web/util)
        │
        ▼
Service (service / service.impl)  ──►  CalculadoraDeVenda (regra de negócio pura)
        │
        ▼
DAO (dao / dao.impl)  ──►  GenericDAO
        │
        ▼
JPA / Hibernate (JPAUtil)
        │
        ▼
MySQL
```

- **Model** — entidades JPA (`Cliente`, `Vendedor`, `Cargo`, `Produto`,
  `Venda`, `Pagamento`).
- **DAO** — acesso a dados genérico (`GenericDAO`) e específico por entidade.
- **Service** — regras de negócio e orquestração de transações.
- **`CalculadoraDeVenda`** — classe de regra de negócio pura (sem
  dependência de DAO/banco), criada para permitir testes unitários
  isolados do cálculo de desconto.
- **`AppContext`** — composição manual das dependências (DAOs e Services),
  usada pelos servlets em vez de um container de injeção de dependência.
- **Servlets (`web/servlet`)** — expõem a API REST consumida pelo front-end.
- **`JsonUtil` (`web/util`)** — padroniza leitura/escrita de JSON nas
  requisições e respostas HTTP (usando Gson).

---

## Tecnologias utilizadas

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Build | Maven |
| Persistência | Jakarta Persistence (JPA) 3.1 + Hibernate 6.4 |
| Banco de dados | MySQL 8 |
| Camada web | Jakarta Servlet API 6.0 |
| Serialização JSON | Gson 2.10 |
| Validação | Jakarta Validation + Hibernate Validator |
| Testes | JUnit 5 (Jupiter) + maven-surefire-plugin |
| Front-end | HTML5, CSS3, JavaScript (ES6+, `fetch`) |
| IDE | NetBeans |
| Servidor de aplicação | **Apache Tomcat EE 10** |

---

## Pré-requisitos

Antes de rodar o projeto, tenha instalado:

1. **JDK 17** ou superior.
2. **NetBeans** (com o plugin/suporte a projetos Maven e Java Web).
3. **Apache Tomcat EE 10** — baixe em
   [tomcat.apache.org](https://tomcat.apache.org/) (distribuição **Tomcat 10**,
   compatível com o namespace `jakarta.*` usado neste projeto) e
   **adicione-o como servidor do projeto no NetBeans** (veja o passo a passo
   abaixo). Versões do Tomcat anteriores à 10 usam o namespace antigo
   `javax.*` e **não são compatíveis** com as dependências deste projeto.
4. **MySQL Server 8** em execução localmente (`localhost:3306`).
5. **Maven** (já integrado ao NetBeans; não é necessário instalar separado).

---

## Configuração do banco de dados

1. Abra o MySQL (Workbench, terminal ou o cliente de sua preferência).
2. Se for a **primeira vez** rodando o projeto, execute o script completo:
   ```sql
   source database/GaleraDaCaneca_database.sql;
   ```
   Ele cria o banco `Galera_da_Caneca` já com todas as correções de tipo
   aplicadas (ver seção [Bugtracking](#bugtracking)).
3. Se você **já tinha o banco de etapas anteriores**, em vez do script
   acima, execute apenas o script de ajustes:
   ```sql
   source database/ajustes_etapa9.sql;
   ```
   Ele corrige as colunas `senha`, `cpf` e `telefone` (que estavam
   tipadas incorretamente como `INT`) e adiciona as colunas `quantidade`
   e `data_venda` à tabela `vendas`.
4. Confira as credenciais de conexão em
   `src/main/resources/META-INF/persistence.xml` e ajuste `usuário`/`senha`
   conforme o seu ambiente local, se necessário:
   ```xml
   <property name="jakarta.persistence.jdbc.url"
             value="jdbc:mysql://localhost:3306/galera_da_caneca"/>
   <property name="jakarta.persistence.jdbc.user" value="root"/>
   <property name="jakarta.persistence.jdbc.password" value="SUA_SENHA_AQUI"/>
   ```

---

## Configuração do servidor (Tomcat) no NetBeans

Este projeto **exige o Apache Tomcat EE 10** instalado na máquina e
registrado como servidor dentro do NetBeans. Passo a passo:

1. Baixe e descompacte o **Apache Tomcat EE 10** em uma pasta de sua
   preferência (ex.: `C:\tomcat10` ou `~/tomcat10`).
2. No NetBeans, abra **Tools → Servers** (ou **Ferramentas → Servidores**).
3. Clique em **Add Server...**, selecione **Apache Tomcat or TomEE** e
   avance.
4. Em **Server Location**, aponte para a pasta onde o Tomcat 10 foi
   descompactado.
5. Defina um usuário/senha de administrador do Tomcat, se solicitado, e
   conclua o assistente.
6. Clique com o botão direito no projeto **`GaleraDaCaneca-WebApp`** →
   **Properties → Run**, e em **Server** selecione o **Apache Tomcat EE 10**
   que acabou de ser adicionado.
7. Confirme que o **Context Path** está como `/galera-da-caneca-webapp`
   (já configurado em `src/main/webapp/META-INF/context.xml`).

---

## Executando o projeto

1. Garanta que o **MySQL** está em execução e que o banco foi criado
   (seção anterior).
2. Abra o projeto no NetBeans: **File → Open Project** e selecione a pasta
   `galera-da-caneca-webapp`.
3. Clique com o botão direito no projeto → **Clean and Build** (executa o
   Maven e gera o `.war` em `target/`).
4. Clique com o botão direito no projeto → **Run** (ou o botão ▶ da barra
   de ferramentas). O NetBeans fará o deploy no Tomcat 10 configurado e
   abrirá o navegador automaticamente em algo como:
   ```
   http://localhost:8080/galera-da-caneca-webapp/
   ```
5. A tela de login (`index.html`) será exibida.

Também é possível gerar o `.war` manualmente, sem o NetBeans:

```bash
mvn clean package
# o artefato final fica em target/galera-da-caneca.war
```
e então implantá-lo manualmente na pasta `webapps/` do Tomcat.

---

## Login de demonstração

Os usuários de demonstração devem existir no banco de dados.  
O cadastro pode ser realizado manualmente via `INSERT` no banco ou pela própria tela de cadastro de funcionários, disponível para um usuário com perfil de gerente.

Diferente da versão simulada da Etapa 8, não existe mais um usuário criado automaticamente no navegador ou em dados locais. Agora, todos os usuários são persistidos no banco de dados real e o login é validado através da API.

Para facilitar a validação da aplicação, os usuários abaixo devem estar cadastrados no banco:

| Nome | Perfil |  Permissões |
|---|---|---|
| Renata Borges | Gerente | Acesso completo: produtos, funcionários, clientes, vendas e painel. |
| Lucas Ribeiro | Vendedor(a) | Acesso a clientes e vendas; sem acesso a produtos e funcionários. |

### Usuários de demonstração

**Gerente**
```
Email: renata.borges@email.com
Senha: 695314
Perfil: Gerente
```

**Vendedor(a)**
```
Email: lucas.ribeiro@email.com
Senha: 583920
Perfil: Vendedor
```

Esses usuários são utilizados apenas para testes e demonstração das permissões de acesso da aplicação.

---

## Estrutura de pastas

```
galera-da-caneca-webapp/
├── database/
│   ├── GaleraDaCaneca_database.sql   → cria o banco do zero (já corrigido)
│   └── ajustes_etapa9.sql            → aplica correções sobre um banco existente
├── evidencias/                        → bugtracking.md e evidencias-de-testes.md (entrega)
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/br/com/galeradacaneca/
│   │   │   ├── dao/                  → interfaces DAO + GenericDAO
│   │   │   ├── dao/impl/             → implementações DAO (Hibernate)
│   │   │   ├── main/Principal.java   → smoke-test manual (ponta a ponta)
│   │   │   ├── model/                → entidades JPA
│   │   │   ├── service/              → interfaces de serviço + CalculadoraDeVenda
│   │   │   ├── service/impl/         → implementações de serviço
│   │   │   ├── util/JPAUtil.java     → EntityManagerFactory
│   │   │   └── web/
│   │   │       ├── AppContext.java   → composição manual das dependências
│   │   │       ├── servlet/          → Auth, Vendedor, Produto, Cliente, Venda, Dashboard
│   │   │       └── util/JsonUtil.java
│   │   ├── resources/META-INF/persistence.xml
│   │   └── webapp/                   → front-end (telas HTML/CSS/JS) + WEB-INF
│   └── test/java/br/com/galeradacaneca/
│       ├── model/VendedorTest.java
│       └── service/CalculadoraDeVendaTest.java
```

---

## API REST

Todos os endpoints estão sob o prefixo `/api` e respondem em JSON. Rotas
que recebem `{id}` esperam o identificador numérico no próprio caminho
(ex.: `/api/produtos/7`).

| Recurso | Endpoint | Métodos |
|---|---|---|
| Autenticação | `/api/auth/login` | `POST` |
| Clientes | `/api/clientes` e `/api/clientes/{id}` | `GET`, `POST`, `PUT`, `DELETE` |
| Produtos | `/api/produtos` e `/api/produtos/{id}` | `GET`, `POST`, `PUT`, `DELETE` |
| Vendedores | `/api/vendedores` e `/api/vendedores/{id}` | `GET`, `POST`, `PUT`, `DELETE` |
| Vendas | `/api/vendas` e `/api/vendas/{id}` | `GET`, `POST`, `DELETE` |
| Painel (dashboard) | `/api/dashboard` | `GET` |

Observações:
- Ao excluir uma venda (`DELETE /api/vendas/{id}`), o servlet exclui
  primeiro o pagamento associado (evitando erro de integridade
  referencial) e devolve a quantidade vendida ao estoque do produto.
- Ao registrar uma venda (`POST /api/vendas`), o servidor valida o
  estoque disponível e calcula o desconto via `CalculadoraDeVenda` antes
  de persistir.

---

## Regra de negócio: desconto por quantidade

Aplicada de forma consistente no back-end (`CalculadoraDeVenda`) e refletida
no front-end (tela de nova venda):

| Quantidade | Desconto |
|---|---|
| Até 4 unidades | 0% |
| De 5 a 9 unidades | 5% |
| 10 unidades ou mais | 10% |

---

## Testes automatizados

```bash
mvn test
```

| Classe | Testes | Cobertura |
|---|---|---|
| `service.CalculadoraDeVendaTest` | 7 | Subtotal, percentual de desconto por faixa, total com desconto, validação de entradas inválidas. |
| `model.VendedorTest` | 4 | Regra `isGerente()` a partir do `Cargo`. |

---
