package org.brentloaf.customCurrencies.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final Connection connection;

    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS accountTable (
                    ownerUuid TEXT NOT NULL,
                    currency TEXT NOT NULL,
                    balance INT NOT NULL,
                    PRIMARY KEY (ownerUuid, currency)
                )
            """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
