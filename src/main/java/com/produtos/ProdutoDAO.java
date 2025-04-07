package com.produtos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.conta.Conta;
import com.db.DB;
import com.escriturario.Escriturario;

public class ProdutoDAO {
    public static ProdutoDAO instance = null;

    public static ProdutoDAO getInstance() {
        if (instance == null) {
            instance = new ProdutoDAO();
        }
        return instance;
    }

    public ArrayList<Produto> getAllProdutos() throws SQLException {
        Connection conn = DB.getConnection();
        ArrayList<Produto> produtos = new ArrayList<>();

        String query = """
                SELECT * FROM tabelaProdutos
                """;
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeQuery();

            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                int id = rs.getInt("id_produto");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                String publicoAlvo = rs.getString("publico-alvo");

                Produto produto = new Produto(id, nome, descricao, publicoAlvo);
                produtos.add(produto);
            }
        }

        return produtos;
    }

    public void venderProduto(Produto produto, Escriturario escriturario, Conta conta) throws SQLException {
        Connection conn = DB.getConnection();

        String query = """
                INSERT INTO tabelaVendaProduto (id_produto, id_escriturario, num_conta)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, escriturario.getId());
            stmt.setInt(3, conta.getNumero());
            stmt.executeUpdate();
        }
    }

    public ArrayList<Produto> getProdutosVendidosPorConta(Conta conta) throws SQLException {
        Connection conn = DB.getConnection();
        ArrayList<Produto> produtos = new ArrayList<>();

        String query = """
                SELECT p.id_produto, p.nome, p.descricao, p.`publico-alvo`
                FROM tabelaProdutos p
                INNER JOIN tabelaVendaProduto vp ON p.id_produto = vp.id_produto
                WHERE vp.num_conta = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, conta.getNumero());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_produto");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                String publicoAlvo = rs.getString("publico-alvo");

                Produto produto = new Produto(id, nome, descricao, publicoAlvo);
                produtos.add(produto);
            }
        }

        return produtos;
    }
}