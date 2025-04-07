package com.conta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import com.db.DB;
import com.escriturario.Escriturario;
import com.escriturario.EscriturarioDAO;
import com.pessoa.PessoaDAO;
import com.pessoa.Pessoa;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {
    private static ContaDAO instance = null;

    public static ContaDAO getInstance() {
        if (instance == null) {
            instance = new ContaDAO();
        }
        return instance;
    }

    public Conta criarConta(ContaDTO dto) throws SQLException {
        Connection conn = DB.getConnection();
        Conta conta = null;

        String query = """
                INSERT INTO tabelaConta (agencia, titular, tipo_conta)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dto.agencia);
            stmt.setInt(2, dto.titular.getId());
            stmt.setString(3, dto.tipoConta.toString());

            stmt.executeUpdate();

            var rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                int id = rs.getInt(1);

                if (dto.tipoConta == TipoConta.Corrente) {
                    conta = new ContaCorrente(id, dto.agencia, dto.titular, 0, 0, LocalDate.now());
                } else {
                    conta = new ContaPoupanca(id, dto.agencia, dto.titular, 0, 0, LocalDate.now());
                }
            } else {
                throw new SQLException("Erro ao criar conta: ID não gerado.");
            }
        }

        return conta;
    }

    public List<Conta> listarContas() throws SQLException {
        Connection conn = DB.getConnection();
        PessoaDAO dao = PessoaDAO.getInstance();
        List<Conta> contas = new ArrayList<>();

        String query = "SELECT * FROM tabelaConta";

        try (PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String agencia = rs.getString("agencia");
                int titular = rs.getInt("titular");
                TipoConta tipoConta = TipoConta.valueOf(rs.getString("tipo_conta"));
                int saldo = rs.getInt("saldo");
                int chequeEspecial = rs.getInt("cheque_especial");
                LocalDate dataAniversario = rs.getDate("data_aniversario").toLocalDate();

                if (tipoConta == TipoConta.Corrente) {
                    contas.add(new ContaCorrente(id, agencia, dao.getById(titular), saldo, chequeEspecial, dataAniversario));
                } else {
                    contas.add(new ContaPoupanca(id, agencia, dao.getById(titular), saldo, chequeEspecial, dataAniversario));
                }
            }
        }

        return contas;
    }

    public List<Conta> listarContasPorCerificado(String certificado) throws SQLException {
        Connection conn = DB.getConnection();
        PessoaDAO dao = PessoaDAO.getInstance();
        List<Conta> contas = new ArrayList<>();

        String query = """
                SELECT c.* FROM tabelaConta c
                INNER JOIN tabelaPessoaFisica pf ON c.titular = pf.id
                WHERE pf.cpf = ?
                UNION
                SELECT c.* FROM tabelaConta c
                INNER JOIN tabelaPessoaJuridica pj ON c.titular = pj.id
                WHERE pj.cnpj = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, certificado);
            stmt.setString(2, certificado);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("num_conta");
                    String agencia = rs.getString("agencia");
                    int titular = rs.getInt("titular");
                    TipoConta tipoConta = rs.getString("tipo_conta") == "C" ? TipoConta.Corrente : TipoConta.Poupanca;
                    int saldo = rs.getInt("saldo");
                    int chequeEspecial = rs.getInt("cheque_especial");
                    LocalDate dataAniversario = rs.getDate("data_aniversario").toLocalDate();

                    Pessoa pessoa = dao.getById(titular);

                    if (tipoConta == TipoConta.Corrente) {
                        contas.add(new ContaCorrente(id, agencia, dao.getById(titular), saldo, chequeEspecial,
                                dataAniversario));
                    } else {
                        contas.add(new ContaPoupanca(id, agencia, dao.getById(titular), saldo, chequeEspecial,
                                dataAniversario));
                    }
                }
            }
        }

        return contas;
    }

    public Conta buscarContaPorId(int id) throws SQLException {
        Connection conn = DB.getConnection();
        PessoaDAO dao = PessoaDAO.getInstance();
        Conta conta = null;

        String query = "SELECT * FROM tabelaConta WHERE num_conta = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String agencia = rs.getString("agencia");
                    int titular = rs.getInt("titular");
                    TipoConta tipoConta = rs.getString("tipo_conta") == "C" ? TipoConta.Corrente : TipoConta.Poupanca;
                    int saldo = rs.getInt("saldo");
                    int chequeEspecial = rs.getInt("cheque_especial");
                    LocalDate dataAniversario = rs.getDate("data_aniversario").toLocalDate();

                    if (tipoConta == TipoConta.Corrente) {
                        conta = new ContaCorrente(id, agencia, dao.getById(titular), saldo, chequeEspecial,
                                LocalDate.now());
                    } else {
                        conta = new ContaPoupanca(id, agencia, dao.getById(titular), saldo, chequeEspecial,
                                dataAniversario);
                    }
                } else {
                    throw new SQLException("Conta não encontrada com o ID fornecido.");
                }
            }
        }

        return conta;
    }

    public boolean deletarConta(int numConta) throws SQLException {
        Connection conn = DB.getConnection();

        String query = "DELETE FROM tabelaConta WHERE num_conta = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numConta);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Erro ao deletar conta: Nenhuma conta encontrada com o ID fornecido.");
            }
        }
        return true;
    }

    public void depositar(Conta conta, int centavos) throws SQLException {
        Escriturario escriturario = EscriturarioDAO.getInstance().getEscriturario();
        Connection conn = DB.getConnection();

        String queryConta = "UPDATE tabelaConta SET saldo = saldo + ? WHERE num_conta = ?";
        String queryOperacao = "INSERT INTO tabelaOperacao (num_conta, id_escriturario, valor, fator, tipo) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmtConta = conn.prepareStatement(queryConta);
                PreparedStatement stmtOperacao = conn.prepareStatement(queryOperacao)) {

            stmtConta.setInt(1, centavos);
            stmtConta.setInt(2, conta.getNumero());
            stmtConta.executeUpdate();

            stmtOperacao.setInt(1, conta.getNumero());
            stmtOperacao.setInt(2, escriturario.getId());
            stmtOperacao.setInt(3, centavos);
            stmtOperacao.setInt(4, 1);
            stmtOperacao.setString(5, "D");

            stmtOperacao.executeUpdate();
        }
    }

    public void sacar(Conta conta, int centavos) throws SQLException {
        Escriturario escriturario = EscriturarioDAO.getInstance().getEscriturario();
        Connection conn = DB.getConnection();

        if (conta.getSaldo() < centavos) {
            throw new SQLException("Saldo insuficiente para realizar o saque.");
        }

        String queryConta = "UPDATE tabelaConta SET saldo = saldo - ? WHERE num_conta = ?";
        String queryOperacao = "INSERT INTO tabelaOperacao (num_conta, id_escriturario, valor, fator, tipo) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmtConta = conn.prepareStatement(queryConta);
                PreparedStatement stmtOperacao = conn.prepareStatement(queryOperacao)) {

            stmtConta.setInt(1, centavos);
            stmtConta.setInt(2, conta.getNumero());
            stmtConta.executeUpdate();

            stmtOperacao.setInt(1, conta.getNumero());
            stmtOperacao.setInt(2, escriturario.getId());
            stmtOperacao.setInt(3, centavos);
            stmtOperacao.setInt(4, -1);
            stmtOperacao.setString(5, "S");

            stmtOperacao.executeUpdate();
        }
    }

    public void transferir(Conta origem, Conta destino, int centavos) throws SQLException {
        Escriturario escriturario = EscriturarioDAO.getInstance().getEscriturario();
        Connection conn = DB.getConnection();

        if (origem.getSaldo() < centavos) {
            throw new SQLException("Saldo insuficiente para realizar a transferência.");
        }

        String queryContaOrigem = "UPDATE tabelaConta SET saldo = saldo - ? WHERE num_conta = ?";
        String queryContaDestino = "UPDATE tabelaConta SET saldo = saldo + ? WHERE num_conta = ?";
        String queryOperacaoOrigem = "INSERT INTO tabelaOperacao (num_conta, id_escriturario, valor, fator, tipo) VALUES (?, ?, ?, ?, ?)";
        String queryOperacaoDestino = "INSERT INTO tabelaOperacao (num_conta, id_escriturario, valor, fator, tipo) VALUES (?, ?, ?, ?, ?)";
        String queryTransferencia = """
                INSERT INTO tabelaTransacao (debito_id, credito_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement stmtContaOrigem = conn.prepareStatement(queryContaOrigem);
                PreparedStatement stmtContaDestino = conn.prepareStatement(queryContaDestino);
                PreparedStatement stmtOperacaoOrigem = conn.prepareStatement(queryOperacaoOrigem, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement stmtOperacaoDestino = conn.prepareStatement(queryOperacaoDestino, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement stmtTransferencia = conn.prepareStatement(queryTransferencia)) {

            // Atualiza o saldo da conta de origem
            stmtContaOrigem.setInt(1, centavos);
            stmtContaOrigem.setInt(2, origem.getNumero());
            stmtContaOrigem.executeUpdate();

            // Atualiza o saldo da conta de destino
            stmtContaDestino.setInt(1, centavos);
            stmtContaDestino.setInt(2, destino.getNumero());
            stmtContaDestino.executeUpdate();

            // Insere a operação na conta de origem
            stmtOperacaoOrigem.setInt(1, origem.getNumero());
            stmtOperacaoOrigem.setInt(2, escriturario.getId());
            stmtOperacaoOrigem.setInt(3, centavos);
            stmtOperacaoOrigem.setInt(4, -1);
            stmtOperacaoOrigem.setString(5, "D");
            stmtOperacaoOrigem.executeUpdate();

            ResultSet rsOperacaoOrigem = stmtOperacaoOrigem.getGeneratedKeys();
            rsOperacaoOrigem.next();

            int debito_id = rsOperacaoOrigem.getInt(1);

            // Insere a operação na conta de destino
            stmtOperacaoDestino.setInt(1, destino.getNumero());
            stmtOperacaoDestino.setInt(2, escriturario.getId());
            stmtOperacaoDestino.setInt(3, centavos);
            stmtOperacaoDestino.setInt(4, 1);
            stmtOperacaoDestino.setString(5, "C");
            stmtOperacaoDestino.executeUpdate();

            ResultSet rsOperacaoDestino = stmtOperacaoDestino.getGeneratedKeys();

            rsOperacaoDestino.next();

            int credito_id = rsOperacaoDestino.getInt(1);

            // Insere a transferência
            stmtTransferencia.setInt(1, debito_id);
            stmtTransferencia.setInt(2, credito_id);
            stmtTransferencia.executeUpdate();
        }
    }

    public List<String> exibirExtrato(Conta conta) throws SQLException {
        Connection conn = DB.getConnection();
        List<String> extrato = new ArrayList<>();

        String query = """
                SELECT o.tempo, o.valor, o.fator, o.tipo
                FROM tabelaOperacao o
                WHERE o.num_conta = ?
                ORDER BY o.tempo DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, conta.getNumero());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dataHora = rs.getString("tempo");
                    int valor = rs.getInt("valor");
                    int fator = rs.getInt("fator");
                    String tipo = rs.getString("tipo");

                    String operacao = String.format("%s | %s | %d.%02d", dataHora, tipo, (valor * fator) / 100,
                            Math.abs((valor * fator) % 100));
                    extrato.add(operacao);
                }
            }
        }

        return extrato;
    }
}