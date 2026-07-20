-- ============================================================
-- AJUSTES_ETAPA9.SQL
-- Execute este script DEPOIS de já ter criado o banco original
-- (GaleraDaCaneca_database.sql). Ele corrige problemas encontrados
-- durante a integração back-end/front-end da Etapa 9 e adiciona as
-- colunas necessárias para a nova funcionalidade web.
--
-- Os problemas abaixo estão registrados no documento de bugtracking
-- (evidencias/bugtracking.md) com os códigos BUG-01 a BUG-04.
-- ============================================================

USE Galera_da_Caneca;

-- BUG-01: coluna "senha" criada como INT e UNIQUE em vendedores e
-- clientes. Isso impede senhas alfanuméricas e impede que dois
-- usuários tenham a mesma senha (o que não é, e não deveria ser,
-- uma regra de negócio do sistema).
ALTER TABLE vendedores DROP INDEX senha;
ALTER TABLE vendedores MODIFY senha VARCHAR(30) NOT NULL;

ALTER TABLE clientes DROP INDEX senha;
ALTER TABLE clientes MODIFY senha VARCHAR(30) NOT NULL;

-- BUG-07: colunas "cpf" (vendedores/clientes) e "telefone" (clientes)
-- criadas como INT. CPF e telefone não são números para fins de
-- cálculo (têm zeros à esquerda e, no caso do CPF mascarado pelo
-- front-end, pontuação). As entidades JPA já mapeiam ambos os campos
-- como String — o INT no banco causaria erro ao inserir.
ALTER TABLE vendedores DROP INDEX cpf;
ALTER TABLE vendedores MODIFY cpf VARCHAR(11) NOT NULL UNIQUE;

ALTER TABLE clientes DROP INDEX cpf;
ALTER TABLE clientes MODIFY cpf VARCHAR(11) NOT NULL UNIQUE;
ALTER TABLE clientes MODIFY telefone VARCHAR(15) NOT NULL;

-- BUG-02: a tabela "vendas" não guardava a quantidade de itens
-- vendidos, apenas o valor_total. Sem essa informação não é possível
-- reexibir o cálculo de desconto (CalculadoraDeVenda) nem validar
-- estoque corretamente a partir de uma venda já registrada.
ALTER TABLE vendas ADD COLUMN quantidade INT NOT NULL DEFAULT 1;

-- BUG-03: a tabela "vendas" não guardava a data/hora da venda,
-- impedindo qualquer ordenação cronológica ou filtro por período
-- nas telas de listagem.
ALTER TABLE vendas ADD COLUMN data_venda DATETIME DEFAULT CURRENT_TIMESTAMP;
UPDATE vendas SET data_venda = CURRENT_TIMESTAMP WHERE data_venda IS NULL;

-- BUG-04: nenhuma venda de exemplo tinha uma quantidade > 1,
-- o que nunca exercitava a faixa de desconto de 5%/10% da
-- CalculadoraDeVenda ao consultar o histórico. Ajuste pontual nos
-- dados de exemplo para cobrir os três cenários de desconto.
UPDATE vendas SET quantidade = 3  WHERE id_vendas IN (1, 2, 3);
UPDATE vendas SET quantidade = 6  WHERE id_vendas IN (4, 5, 6);
UPDATE vendas SET quantidade = 12 WHERE id_vendas IN (7, 8, 9, 10);

-- Verificação final (conferir no console do MySQL / DBeaver / etc.)
-- DESCRIBE vendedores;
-- DESCRIBE clientes;
-- DESCRIBE vendas;
