package org.brentloaf.customCurrencies.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {

    private static Connection connection;

    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS bankTable (
                    id TEXT NOT NULL,
                    bankName TEXT NOT NULL,
                    ownerId TEXT NOT NULL,
                    currenciesIds TEXT NOT NULL,
                    PRIMARY KEY (uuid, bankName, ownerUuid)
                )
            """);
        }
    }

    public static class Bank {

        public static final String TABLE_NAME = "bankTable";

        public static void add(UUID ownerId, String name) {
            String sql = "INSERT INTO " + TABLE_NAME + " (id, bankName, ownerId, currenciesIds) VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, UUID.randomUUID().toString());
                statement.setString(2, name);
                statement.setString(3, ownerId.toString());
                statement.setString(4, "[]");

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static void addCurrency(UUID id, UUID currencyId) {
            String sqlSelect = "SELECT currenciesIds FROM " + TABLE_NAME + " WHERE id = ?";
            List<String> currencies = new ArrayList<>();

            try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
                statement.setString(1, id.toString());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        Type type = new TypeToken<List<String>>(){}.getType();
                        String json = result.getString("currenciesIds");

                        if (json != null) currencies = new ArrayList<>(new Gson().fromJson(json, type));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            currencies.add(currencyId.toString());
            String updatedJson = new Gson().toJson(currencies);

            String sqlUpdate = "UPDATE " + TABLE_NAME + " SET currenciesIds = ? WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
                statement.setString(1, updatedJson);
                statement.setString(2, id.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }
}
