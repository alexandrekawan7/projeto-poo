package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/atm"; // nome do banco aqui
                String user = "root"; // ou seu usuário do MySQL
                String password = "@Linkdesign5"; // troque pela sua senha
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                System.out.println("Erro na conexão: " + e.getMessage());
            }
        }
        return connection;
    }

}