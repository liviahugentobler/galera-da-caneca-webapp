-- criando base de dados 
CREATE DATABASE Galera_da_Caneca;
USE Galera_da_Caneca;

-- criação de tabelas 

CREATE TABLE clientes(
	id_clientes INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    nome_completo VARCHAR(100) NOT NULL,
    nascimento DATE NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    email VARCHAR(45) NOT NULL UNIQUE,
    senha VARCHAR(30) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE, 
    endereco VARCHAR(45) NOT NULL,
    sexo VARCHAR(2) NOT NULL
);

CREATE TABLE vendedores(
	id_vendedores INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    nome_completo VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE, 
    nascimento DATE NOT NULL, 
    email VARCHAR(45) NOT NULL UNIQUE,
    senha VARCHAR(30) NOT NULL,
    sexo CHAR(1),
    id_cargo INT 
);

CREATE TABLE cargos(
	id_cargo INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(15) NOT NULL UNIQUE
);

CREATE TABLE produtos(
	id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome_prod VARCHAR(45) NOT NULL UNIQUE,
    preco DECIMAL(10,2) NOT NULL,
    quantidade INT NOT NULL UNIQUE,
    saidas INT NOT NULL,
    entradas INT NOT NULL
);

CREATE TABLE vendas(
	id_vendas INT AUTO_INCREMENT PRIMARY KEY,
    id_vendedores INT,
    id_produto INT, 
    id_cliente INT, 
    valor_total DECIMAL(10,2),
    quantidade INT DEFAULT 1,
    data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_vendedores) REFERENCES vendedores(id_vendedores),
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_clientes)
);

CREATE TABLE pagamentos(
	id_pagamentos INT AUTO_INCREMENT NOT NULL UNIQUE PRIMARY KEY,
    id_vendas INT,
	valor_total DECIMAL (10,2),
    forma_pagamento VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_vendas) REFERENCES vendas(id_vendas)
);

-- alterar valores das tabelas
ALTER TABLE clientes MODIFY sexo CHAR(1) NOT NULL;
ALTER TABLE vendedores MODIFY id_cargo INT;
ALTER TABLE vendedores ADD CONSTRAINT fk_id_cargos FOREIGN KEY (id_cargo) REFERENCES cargos(id_cargo); 
ALTER TABLE clientes MODIFY telefone VARCHAR(15) NOT NULL;
ALTER TABLE clientes MODIFY cpf VARCHAR(11) NOT NULL UNIQUE;
ALTER TABLE vendedores MODIFY cpf VARCHAR(11) NOT NULL;

SHOW INDEXES FROM vendedores;
DROP INDEX cpf_2 ON vendedores;

SHOW INDEXES FROM clientes;
DROP INDEX cpf_2 ON clientes;

ALTER TABLE produtos DROP INDEX quantidade;
ALTER TABLE produtos MODIFY quantidade INT NOT NULL;

ALTER TABLE vendas MODIFY COLUMN id_vendedores INT NULL;

ALTER TABLE vendas MODIFY COLUMN id_produto INT NULL;


-- inserindo dados na tabela 
INSERT INTO clientes (nome_completo, nascimento, telefone, email, senha, cpf, endereco, sexo) -- feito para teste de inserção 
VALUES ('Lívia Hugentobler', '2007-05-28', '51994377829', 'livia.hugentobler@gmail.com', '280507', 04754139038, 'R. Sebastião Bristotte', 'F');

INSERT INTO clientes (nome_completo, nascimento, telefone, email, senha, cpf, endereco, sexo) VALUES
('Ana Paula Mendes', '1995-03-12', '51982345678', 'ana.mendes@email.com', '583920', '12345678901', 'Rua das Acácias, 102', 'F'),
('Bruno César Lopes', '1988-07-25', '51999784512', 'bruno.lopes@email.com', '147893', '98765432100', 'Av. Brasil, 500', 'M'),
('Carla Rodrigues', '2001-11-03', '51990011223', 'carla.rod@email.com', '920471', '11122233344', 'Rua A, 45', 'F'),
('Daniel Souza', '1999-05-19', '51995123456', 'daniel.souza@email.com', '306258', '55566677788', 'Rua Central, 88', 'M'),
('Eduarda Lima', '2004-09-14', '51999887766', 'eduarda.lima@email.com', '781423', '33344455566', 'Av. da Paz, 120', 'F'),
('Felipe Andrade', '1990-02-28', '51995566778', 'felipe.andrade@email.com', '695314', '77788899900', 'Rua Nova, 300', 'M'),
('Gabriela Martins', '1998-12-10', '51991112233', 'gabi.martins@email.com', '218940', '22233344455', 'Rua dos Lírios, 11', 'F'),
('Henrique Oliveira', '2003-06-30', '51992233445', 'henrique.oli@email.com', '874120', '66677788899', 'Rua São José, 200', 'M'),
('Isabela Nunes', '1996-04-08', '51993344556', 'isa.nunes@email.com', '391756', '44455566677', 'Av. Esperança, 75', 'F'),
('João Victor Silva', '2000-10-20', '51994455667', 'joao.silva@email.com', '460298', '88899900011', 'Rua das Palmeiras, 9', 'M');

INSERT INTO cargos (descricao) VALUES
('Vendedor'),
('Gerente');

INSERT INTO vendedores (nome_completo, cpf, nascimento, email, senha, sexo, id_cargo) VALUES
('Lucas Ribeiro', '12345678900', '1990-04-15', 'lucas.ribeiro@email.com', '583920', 'M', 1),
('Amanda Costa', '23456789011', '1995-07-20', 'amanda.costa@email.com', '147893', 'F', 1),
('Thiago Martins', '34567890122', '1988-01-10', 'thiago.martins@email.com', '920471', 'M', 1),
('Juliana Rocha', '45678901233', '1993-12-05', 'juliana.rocha@email.com', '306258', 'F', 1),
('Marcos Lima', '56789012344', '1997-06-18', 'marcos.lima@email.com', '781423', 'M', 1),
('Renata Borges', '67890123455', '1985-09-25', 'renata.borges@email.com', '695314', 'F', 2),
('Fernando Alves', '78901234566', '1982-03-30', 'fernando.alves@email.com', '218940', 'M', 2);

INSERT INTO produtos(nome_prod, preco, quantidade, saidas, entradas) VALUES
('Caneca Star Wars', 29.90, 85, 15, 100),
('Caneca Harry Potter', 34.90, 92, 8, 100),
('Caneca Friends', 27.50, 70, 30, 100),
('Caneca Marvel', 32.00, 60, 40, 100),
('Caneca Batman', 28.70, 76, 24, 100),
('Caneca Lord of the Rings', 31.20, 64, 36, 100),
('Caneca Game of Thrones', 33.90, 50, 50, 100),
('Caneca Stranger Things', 30.00, 90, 10, 100),
('Caneca Pokémon', 26.90, 78, 22, 100),
('Caneca Rick and Morty', 35.50, 84, 16, 100),
('Caneca Naruto', 29.00, 95, 5, 100),
('Caneca Gato Preto', 25.90, 88, 12, 100),
('Caneca Café é Vida', 22.50, 98, 2, 100),
('Caneca Personalizada', 39.90, 55, 45, 100),
('Caneca Minimalista', 24.90, 73, 27, 100),
('Caneca Motivacional', 27.00, 91, 9, 100),
('Caneca Geek', 30.90, 82, 18, 100),
('Caneca Vintage', 31.50, 66, 34, 100),
('Caneca Musical', 28.00, 80, 20, 100),
('Caneca Astronauta', 36.00, 62, 38, 100);

INSERT INTO vendas (id_vendedores, id_produto, id_cliente, valor_total) VALUES
(1, 1, 1, 29.90),
(2, 2, 2, 34.90),
(3, 3, 3, 27.50),
(4, 4, 4, 32.00),
(5, 5, 5, 28.70),
(6, 6, 6, 31.20),
(7, 7, 7, 33.90),
(1, 8, 8, 30.00),
(2, 9, 9, 26.90),
(3, 10, 10, 35.50),

(4, 11, 1, 29.00),
(5, 12, 2, 25.90),
(6, 13, 3, 22.50),
(7, 14, 4, 39.90),
(1, 15, 5, 24.90),
(2, 16, 6, 27.00),
(3, 17, 7, 30.90),
(4, 18, 8, 31.50),
(5, 19, 9, 28.00),
(6, 20, 10, 36.00),

(7, 1, 1, 29.90),
(1, 2, 2, 34.90),
(2, 3, 3, 27.50),
(3, 4, 4, 32.00),
(4, 5, 5, 28.70),
(5, 6, 6, 31.20),
(6, 7, 7, 33.90),
(7, 8, 8, 30.00),
(1, 9, 9, 26.90),
(2, 10, 10, 35.50),

(3, 11, 1, 29.00),
(4, 12, 2, 25.90),
(5, 13, 3, 22.50),
(6, 14, 4, 39.90),
(7, 15, 5, 24.90),
(1, 16, 6, 27.00),
(2, 17, 7, 30.90),
(3, 18, 8, 31.50),
(4, 19, 9, 28.00),
(5, 20, 10, 36.00),

(6, 1, 1, 29.90),
(7, 2, 2, 34.90),
(1, 3, 3, 27.50),
(2, 4, 4, 32.00),
(3, 5, 5, 28.70),
(4, 6, 6, 31.20),
(5, 7, 7, 33.90),
(6, 8, 8, 30.00),
(7, 9, 9, 26.90),
(1, 10, 10, 35.50);

INSERT INTO pagamentos (id_vendas, valor_total, forma_pagamento) VALUES
(1, 29.90, 'Dinheiro'),
(2, 34.90, 'Cartão'),
(3, 27.50, 'Pix'),
(4, 32.00, 'Boleto'),
(5, 28.70, 'Dinheiro'),
(6, 31.20, 'Cartão'),
(7, 33.90, 'Pix'),
(8, 30.00, 'Boleto'),
(9, 26.90, 'Dinheiro'),
(10, 35.50, 'Cartão'),

(11, 29.00, 'Pix'),
(12, 25.90, 'Boleto'),
(13, 22.50, 'Dinheiro'),
(14, 39.90, 'Cartão'),
(15, 24.90, 'Pix'),
(16, 27.00, 'Boleto'),
(17, 30.90, 'Dinheiro'),
(18, 31.50, 'Cartão'),
(19, 28.00, 'Pix'),
(20, 36.00, 'Boleto'),

(21, 29.90, 'Dinheiro'),
(22, 34.90, 'Cartão'),
(23, 27.50, 'Pix'),
(24, 32.00, 'Boleto'),
(25, 28.70, 'Dinheiro'),
(26, 31.20, 'Cartão'),
(27, 33.90, 'Pix'),
(28, 30.00, 'Boleto'),
(29, 26.90, 'Dinheiro'),
(30, 35.50, 'Cartão'),

(31, 29.00, 'Pix'),
(32, 25.90, 'Boleto'),
(33, 22.50, 'Dinheiro'),
(34, 39.90, 'Cartão'),
(35, 24.90, 'Pix'),
(36, 27.00, 'Boleto'),
(37, 30.90, 'Dinheiro'),
(38, 31.50, 'Cartão'),
(39, 28.00, 'Pix'),
(40, 36.00, 'Boleto'),

(41, 29.90, 'Dinheiro'),
(42, 34.90, 'Cartão'),
(43, 27.50, 'Pix'),
(44, 32.00, 'Boleto'),
(45, 28.70, 'Dinheiro'),
(46, 31.20, 'Cartão'),
(47, 33.90, 'Pix'),
(48, 30.00, 'Boleto'),
(49, 26.90, 'Dinheiro'),
(50, 35.50, 'Cartão');

-- buscando com select

SELECT*FROM clientes;

SELECT*FROM produtos WHERE nome_prod = 'Caneca Lord of the Rings'; 

SELECT*FROM vendedores;

SELECT*FROM vendas WHERE id_vendedores = 1;

SELECT*FROM cargos;

SELECT*FROM pagamentos WHERE valor_total = 36;

SELECT*FROM pagamentos WHERE forma_pagamento = 'Dinheiro';

SELECT * FROM pagamentos WHERE id_vendas = 50;


-- atualizando tabela c/ update

UPDATE clientes SET telefone = '51988887777' WHERE id_clientes = 2;

UPDATE vendedores SET id_cargo = 2 WHERE id_vendedores = 2;

UPDATE cargos SET descricao = 'Supervisor' WHERE id_cargo = 2;

UPDATE produtos SET preco = 34.00 WHERE id_produto = 4;

UPDATE vendas SET valor_total = 40.00 WHERE id_vendas = 10;

UPDATE pagamentos SET forma_pagamento = 'Pix' WHERE id_pagamentos = 15;

UPDATE vendas SET id_vendedores = NULL WHERE id_vendedores = 7;

UPDATE vendas SET id_produto = NULL WHERE id_produto = 12;

-- deletando dados (delete/drop)
DELETE FROM clientes WHERE id_clientes = 11;

DELETE FROM vendedores WHERE id_vendedores = 7;

DELETE FROM produtos WHERE id_produto = 12;

DELETE FROM pagamentos WHERE id_pagamentos = 50;

DELETE FROM pagamentos WHERE id_vendas = 50;

DELETE FROM vendas WHERE id_vendas = 50;
