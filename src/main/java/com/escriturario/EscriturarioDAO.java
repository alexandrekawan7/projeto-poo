package com.escriturario;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.db.DB;

public class EscriturarioDAO {
    private static EscriturarioDAO instance = null;

    public static EscriturarioDAO getInstance() {
        if (instance == null) {
            instance = new EscriturarioDAO();
        }
        return instance;
    }

    public Escriturario getEscriturario() {
        Escriturario escriturario = new Escriturario();

        Connection conn = DB.getConnection();

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tabelaEscriturario LIMIT 1");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                escriturario.setIdEscriturario(rs.getInt("id_escriturario"));
                escriturario.setNome(rs.getString("nome"));
                escriturario.setCpf(rs.getString("cpf"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return escriturario;
    }
}
