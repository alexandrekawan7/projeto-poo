package com.pessoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.db.DB;

public class PessoaDAO {
    public static PessoaDAO instance = null;

    public static PessoaDAO getInstance() {
        if (instance == null) {
            instance = new PessoaDAO();
        }
        return instance;
    }

    public Pessoa getById(int id) throws SQLException {
        Connection conn = DB.getConnection();
        Pessoa pessoa = null;

        String query = """
                SELECT p.id, p.nome, p.email, p.endereco, p.renda, p.telefone, 
                       pf.cpf, pj.cnpj 
                FROM tabelaPessoa p
                LEFT JOIN tabelaPessoaFisica pf ON p.id = pf.id
                LEFT JOIN tabelaPessoaJuridica pj ON p.id = pj.id
                WHERE p.id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String endereco = rs.getString("endereco");
                int renda = rs.getInt("renda");
                String telefone = rs.getString("telefone");
                String cpf = rs.getString("cpf");
                String cnpj = rs.getString("cnpj");

                if (cpf != null) {
                    pessoa = new PessoaFisica(id, nome, email, endereco, renda, telefone, cpf);
                } else if (cnpj != null) {
                    pessoa = new PessoaJuridica(id, nome, email, endereco, renda, telefone, cnpj);
                }
            }
        }

        return pessoa;
    }

    public Pessoa criarPessoa(PessoaDTO dto) throws SQLException {
        Connection conn = DB.getConnection();
        Pessoa pessoa = null;

        String query = """
                INSERT INTO tabelaPessoa (nome, email, endereco, renda, telefone) VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dto.nome);
            stmt.setString(2, dto.email);
            stmt.setString(3, dto.endereco);
            stmt.setInt(4, dto.renda);
            stmt.setString(5, dto.telefone);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            rs.next();

            int idPessoa = rs.getInt(1);

            if (dto.tipoPessoa == TipoPessoa.Fisica) {

                try (PreparedStatement stmt2 = conn.prepareStatement("""
                        INSERT INTO tabelaPessoaFisica (id, cpf) VALUES (?, ?)
                        """)) {
                    
                    stmt2.setInt(1, idPessoa);
                    stmt2.setString(2, dto.certificado);

                    stmt2.executeUpdate();
                }

                pessoa = new PessoaFisica(idPessoa, dto.nome, dto.email, dto.endereco, dto.renda, dto.telefone, dto.certificado);
            } else if (dto.tipoPessoa == TipoPessoa.Juridica) {
                try (PreparedStatement stmt2 = conn.prepareStatement("""
                        INSERT INTO tabelaPessoaJuridica (id, cnpj) VALUES (?, ?)
                        """)) {
                    
                    stmt2.setInt(1, idPessoa);
                    stmt2.setString(2, dto.certificado);

                    stmt2.executeUpdate();
                }

                pessoa = new PessoaJuridica(idPessoa, dto.nome, dto.email, dto.endereco, dto.renda, dto.telefone, dto.certificado);
            }
        }

        return pessoa;
    }

    public Pessoa getByCpf(String certificate) throws SQLException {
        Connection conn = DB.getConnection();
        Pessoa pessoa = null;

        String query = """
                SELECT p.id, p.nome, p.email, p.endereco, p.renda, p.telefone, 
                       pf.cpf, pj.cnpj 
                FROM tabelaPessoa p
                LEFT JOIN tabelaPessoaFisica pf ON p.id = pf.id
                LEFT JOIN tabelaPessoaJuridica pj ON p.id = pj.id
                WHERE pf.cpf = ? OR pj.cnpj = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, certificate);
            stmt.setString(2, certificate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String endereco = rs.getString("endereco");
                int renda = rs.getInt("renda");
                String telefone = rs.getString("telefone");
                String cpf = rs.getString("cpf");
                String cnpj = rs.getString("cnpj");

                if (cpf != null) {
                    pessoa = new PessoaFisica(id, nome, email, endereco, renda, telefone, cpf);
                } else if (cnpj != null) {
                    pessoa = new PessoaJuridica(id, nome, email, endereco, renda, telefone, cnpj);
                }
            }
        }

        return pessoa;
    }

    public boolean update(Pessoa pessoa, PessoaDTO dto) throws SQLException {
        Connection conn = DB.getConnection();
        boolean updated = false;

        String query = """
                UPDATE tabelaPessoa 
                SET nome = ?, email = ?, endereco = ?, renda = ?, telefone = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dto.nome);
            stmt.setString(2, dto.email);
            stmt.setString(3, dto.endereco);
            stmt.setInt(4, dto.renda);
            stmt.setString(5, dto.telefone);
            stmt.setInt(6, pessoa.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                updated = true;

                if (dto.tipoPessoa == TipoPessoa.Fisica) {
                    try (PreparedStatement stmt2 = conn.prepareStatement("""
                            UPDATE tabelaPessoaFisica 
                            SET cpf = ? 
                            WHERE id = ?
                            """)) {
                        stmt2.setString(1, dto.certificado);
                        stmt2.setInt(2, pessoa.getId());
                        stmt2.executeUpdate();
                    }
                } else if (dto.tipoPessoa == TipoPessoa.Juridica) {
                    try (PreparedStatement stmt2 = conn.prepareStatement("""
                            UPDATE tabelaPessoaJuridica 
                            SET cnpj = ? 
                            WHERE id = ?
                            """)) {
                        stmt2.setString(1, dto.certificado);
                        stmt2.setInt(2, pessoa.getId());
                        stmt2.executeUpdate();
                    }
                }
            }
        }

        return updated;
    }

    public boolean delete(int id) throws SQLException {
        Connection conn = DB.getConnection();
        boolean deleted = false;

        String queryPessoa = "DELETE FROM tabelaPessoa WHERE id = ?";

        try (PreparedStatement stmtPessoa = conn.prepareStatement(queryPessoa)) {
            stmtPessoa.setInt(1, id);
            int rowsAffected = stmtPessoa.executeUpdate();

            if (rowsAffected > 0) {
                deleted = true;
            }
        }

        return deleted;
    }
}
