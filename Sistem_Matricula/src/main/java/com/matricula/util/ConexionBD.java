package com.matricula.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexionBD {

    private static HikariDataSource dataSource;

     
    private static final String HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String URL = "jdbc:mysql://" + HOST + ":3306/bd_matricula?useSSL=false&serverTimezone=America/Lima";
    private static final String USUARIO = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String CLAVE = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : "";

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USUARIO);
        config.setPassword(CLAVE);
        config.setMaximumPoolSize(10);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource = new HikariDataSource(config);
    }

    private ConexionBD() {
    }

    public static Connection getConexion() throws SQLException {
        return dataSource.getConnection();
    }
}
