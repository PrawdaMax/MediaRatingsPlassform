package org.example.pkgDB;

import org.example.pkgMisc.Config;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.*;
import org.example.pkgServer.pkgToken.JWTUtil;

import java.sql.*;
import java.util.*;

public class Database {
    private static final String URL = Config.get("DB_URL");
    private static final String USER = Config.get("DB_USER");
    private static final String PASSWORD = Config.get("DB_PASSWORD");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
