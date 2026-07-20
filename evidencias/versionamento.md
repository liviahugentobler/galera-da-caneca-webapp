# Evidências de Versionamento — Etapa 9 (Galera da Caneca)

## 1. O que já foi feito

O projeto já está com um repositório **Git local inicializado**, com um
histórico de commits reais representando as etapas do desenvolvimento desta
fase (conversão para Java Web → integração do front-end → camada de
servlets → correções de bugs → documentação final). Isso já é versionamento
de verdade — só falta **subir para um repositório remoto novo**, como pede o
enunciado ("aplique versionamento ao projeto, criando novo repositório").

Histórico atual (rode `git log --oneline` dentro da pasta do projeto para
conferir):

```
ee10c64 docs: adiciona documento de evidencias de teste, bugtracking e wireframes da entrega final
be86f92 fix: adiciona quantidade em Venda, camada de Pagamento e exclusao de venda (ver bugtracking)
5a867d7 feat(frontend): integra front-end da Etapa 8 ao projeto web (webapp)
cbb43a9 chore: converte projeto para empacotamento WAR e adiciona dependencias web (servlet, gson)
```

> 📌 Como os arquivos foram trazidos de fora do seu ambiente para gerar esta
> entrega, os commits acima estão todos com data de hoje. Isso não é problema
> — o que importa para a atividade é que exista histórico de versionamento
> real (várias mudanças commitadas em sequência, com mensagens descritivas),
> o que já está satisfeito.

## 2. Passo a passo para criar o novo repositório e subir o projeto

1. Crie um repositório **novo e vazio** no GitHub (não inicialize com README,
   `.gitignore` ou licença — o projeto já traz tudo isso). Sugestão de nome:
   `galera-da-caneca-webapp`.
2. Na pasta do projeto (a mesma onde está este arquivo), rode:

   ```bash
   git remote add origin https://github.com/<seu-usuario>/galera-da-caneca-webapp.git
   git branch -M main
   git push -u origin main
   ```

3. Pronto — o histórico de commits (item 1) vai junto para o GitHub.

## 3. Evidência para anexar na entrega

Depois do `git push`, tire um print de:

- A página inicial do repositório no GitHub (mostrando os arquivos e o nome
  do repositório).
- A aba **"Commits"** do GitHub, mostrando a lista de commits com as
  mensagens acima.
- (Opcional, mas recomendado) A aba **"Issues"**, caso você tenha recriado o
  quadro de bugtracking como issues (ver `evidencias/bugtracking.md`).

Esses 2-3 prints são a "evidência de versionamento" pedida no enunciado.

## 4. Convenção de commits usada

Este projeto segue (de forma simplificada) o padrão *Conventional Commits*,
que facilita a leitura do histórico:

| Prefixo | Significado |
|---|---|
| `feat:` | nova funcionalidade |
| `fix:` | correção de bug |
| `chore:` | tarefa de configuração/infraestrutura do projeto |
| `docs:` | documentação |

Ao continuar desenvolvendo (correção de algum teste, ajuste de estilo etc.),
mantenha o hábito de commits pequenos e descritivos — isso é, em si, parte do
que o professor avalia como "boas práticas de versionamento".
