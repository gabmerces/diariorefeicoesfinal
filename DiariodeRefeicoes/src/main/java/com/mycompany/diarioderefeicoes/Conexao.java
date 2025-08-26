package com.mycompany.diarioderefeicoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:mysql://localhost:3306/diario_refeicoes?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão estabelecida com sucesso.");
            return conn;
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro: Driver JDBC não encontrado.");
            ex.printStackTrace();
            throw new SQLException("Driver JDBC não encontrado.", ex);
        }
    }
}
