DROP SCHEMA atm;

CREATE DATABASE atm;

USE atm;

START TRANSACTION;

-- Tabela Escriturário
CREATE TABLE tabelaEscriturario (
    id_escriturario INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    cpf CHAR(11)
);

INSERT INTO tabelaEscriturario (nome, cpf) VALUES ('Junin', '00000000000');

-- Tabela Pessoa
CREATE TABLE tabelaPessoa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    email VARCHAR(100),
    endereco VARCHAR(150),
    renda INT,
    telefone VARCHAR(15)
);

-- Tabela Pessoa Física
CREATE TABLE tabelaPessoaFisica (
    id INT PRIMARY KEY,
    cpf VARCHAR(14) UNIQUE,
    FOREIGN KEY (id) REFERENCES tabelaPessoa(id) ON DELETE CASCADE
);

-- Tabela Pessoa Jurídica
CREATE TABLE tabelaPessoaJuridica (
    id INT PRIMARY KEY,
    cnpj VARCHAR(18) UNIQUE,
    FOREIGN KEY (id) REFERENCES tabelaPessoa(id) ON DELETE CASCADE
);

-- Tabela Conta
CREATE TABLE tabelaConta (
    num_conta INT PRIMARY KEY AUTO_INCREMENT,
    agencia VARCHAR(10),
    titular INT,
    saldo INT DEFAULT 0, 
    cheque_especial INT DEFAULT 0,
    tipo_conta CHAR(1), # C - Corrente, P - Poupança,
    data_aniversario DATE DEFAULT (CURRENT_DATE()),
    FOREIGN KEY (titular) REFERENCES tabelaPessoa(id) ON DELETE CASCADE
);

-- Tabela Produtos
CREATE TABLE tabelaProdutos (
    id_produto INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    descricao TEXT,
    `publico-alvo` VARCHAR(50)
);

INSERT INTO tabelaProdutos (nome, descricao, `publico-alvo`)
VALUES
('Conta Corrente', 'Conta bancária para movimentações do dia a dia, com acesso a cartão de débito, crédito e internet banking.', 'Pessoa Física'),
('Conta Empresarial', 'Conta corrente voltada para empresas, com serviços como cobrança, folha de pagamento e gestão financeira.', 'Pessoa Jurídica'),
('Cartão de Crédito', 'Cartão com limite pré-aprovado para compras, com opção de parcelamento e programa de pontos.', 'Pessoa Física'),
('Empréstimo Pessoal', 'Crédito com pagamento parcelado, taxas diferenciadas e análise rápida.', 'Pessoa Física'),
('Financiamento de Veículos', 'Produto para aquisição de automóveis com prazos estendidos e taxas competitivas.', 'Pessoa Física'),
('Maquininha de Cartão', 'Solução para recebimento de pagamentos com cartões de crédito e débito.', 'Pessoa Jurídica'),
('Investimentos CDB', 'Certificado de Depósito Bancário com rentabilidade fixa ou pós-fixada.', 'Pessoa Física'),
('Capital de Giro', 'Linha de crédito voltada para manter as operações do negócio em dia.', 'Pessoa Jurídica');

-- Tabela Venda Produto
CREATE TABLE tabelaVendaProduto (
    id_produto INT,
    id_escriturario INT,
    num_conta INT,
    PRIMARY KEY (id_produto, id_escriturario, num_conta),
    FOREIGN KEY (id_produto) REFERENCES tabelaProdutos(id_produto) ON DELETE CASCADE,
    FOREIGN KEY (id_escriturario) REFERENCES tabelaEscriturario(id_escriturario) ON DELETE CASCADE,
    FOREIGN KEY (num_conta) REFERENCES tabelaConta(num_conta) ON DELETE CASCADE
);

-- Tabela Operação
CREATE TABLE tabelaOperacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    num_conta INT,
    id_escriturario INT,
    tempo DATETIME DEFAULT CURRENT_TIMESTAMP(),
    valor INT,
    fator INT, # -1 ou 1
    tipo CHAR(1), # C/D/S/D
    FOREIGN KEY (num_conta) REFERENCES tabelaConta(num_conta) ON DELETE CASCADE,
    FOREIGN KEY (id_escriturario) REFERENCES tabelaEscriturario(id_escriturario) ON DELETE CASCADE
);

-- Tabela Transação
CREATE TABLE tabelaTransacao (
	debito_id INT, # Operação de quem debitou
	credito_id INT, #  Operação de quem recebeu
	FOREIGN KEY (debito_id) REFERENCES tabelaOperacao(id) ON DELETE CASCADE,
	FOREIGN KEY (credito_id) REFERENCES tabelaOperacao(id) ON DELETE CASCADE
);

COMMIT;

